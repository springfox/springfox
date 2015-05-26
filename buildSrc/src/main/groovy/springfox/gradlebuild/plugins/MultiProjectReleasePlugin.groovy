/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.gradlebuild.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import springfox.gradlebuild.BintrayCredentials
import springfox.gradlebuild.BuildInfo
import springfox.gradlebuild.BuildInfoFactory
import springfox.gradlebuild.tasks.*
import springfox.gradlebuild.version.GitDescribeVersioningStrategy
import springfox.gradlebuild.version.VersioningStrategy

/**
 * Much of what this plugin does is inspired by:
 * https://www.youtube.com/watch?v=Y6SVoXFsw7I ( GradleSummit2014 - Releasing With Gradle - René Groeschke)
 *
 */
public class MultiProjectReleasePlugin implements Plugin<Project> {

  private static Logger LOG = Logging.getLogger(MultiProjectReleasePlugin.class);
  ReleaseTask releaseTask
  BumpAndTagTask bumpAndTagTask
  CheckCleanWorkspaceTask checkCleanWorkspaceTask
  SnapshotTask snapshotTask
  BintrayCredentialsCheckTask credentialCheck
  CheckGitBranchTask checkGitBranchTask
  Task showPublishInfo
  VersioningStrategy versioningStrategy

  @Override
  void apply(Project project) {
    versioningStrategy = GitDescribeVersioningStrategy.create(buildNumberFormat(project))
    BuildInfo versioningInfo = createBuildInfo(project, versioningStrategy)
    releaseTask = project.task(ReleaseTask.TASK_NAME, type: ReleaseTask) {
      buildInfo = versioningInfo
    }
    bumpAndTagTask = project.task(BumpAndTagTask.TASK_NAME, type: BumpAndTagTask) {
      buildInfo = versioningInfo
      versioningStrategy = this.versioningStrategy
    }
    snapshotTask = project.task(SnapshotTask.TASK_NAME, type: SnapshotTask)
    credentialCheck = project.task(BintrayCredentialsCheckTask.TASK_NAME, type: BintrayCredentialsCheckTask)
    checkCleanWorkspaceTask = project.task(CheckCleanWorkspaceTask.TASK_NAME, type: CheckCleanWorkspaceTask)
    checkGitBranchTask = project.task(CheckGitBranchTask.TASK_NAME, type: CheckGitBranchTask)
    showPublishInfo = project.task('showPublishInfo') {
      group = 'Help'
      description = 'Show project publishing information'
    }
    project.tasks.showPublishInfo << {
      LOG.info "================== Project version: $project.version, $versioningInfo"
    }

    configureSnapshotTaskGraph(project)
    configureReleaseTaskGraph(project)
    configureVersionAndPublications(project, versioningInfo)
  }

  def configureSnapshotTaskGraph(Project project) {
    def iSnapshotCheckTask = project.task('iSnapshotCheck', type: IntermediaryTask)
    iSnapshotCheckTask.dependsOn showPublishInfo
    project.afterEvaluate { evaluatedProject ->
      def javaCheckTasks = evaluatedProject.getTasksByName('check', true)
      iSnapshotCheckTask.dependsOn javaCheckTasks

      evaluatedProject.subprojects.each {
        def jcenterTasks = it.tasks.findAll { it.name.contains('ToJcenterRepository') }
        if (jcenterTasks) {
          snapshotTask.dependsOn jcenterTasks
        }
      }
    }

    snapshotTask.dependsOn iSnapshotCheckTask
    snapshotTask.dependsOn checkCleanWorkspaceTask
    snapshotTask.dependsOn credentialCheck
    iSnapshotCheckTask.mustRunAfter checkCleanWorkspaceTask
    iSnapshotCheckTask.mustRunAfter credentialCheck

  }

  def configureReleaseTaskGraph(Project project) {
    def iPublishTask = project.task('iPublishTask', type: IntermediaryTask)
    def iCheckTask = project.task('iCheckTask', type: IntermediaryTask)
    def iWorkspaceTask = project.task('iWorkspaceTask', type: IntermediaryTask)

    project.afterEvaluate { evaluatedProject ->
      def javaCheckTasks = evaluatedProject.getTasksByName('check', true)
      iCheckTask.dependsOn javaCheckTasks

      evaluatedProject.subprojects.each {
        def bintrayPublishTask = it.tasks.findByPath('bintrayUpload')
        if (bintrayPublishTask) {
          iPublishTask.dependsOn(bintrayPublishTask)
        }
      }
    }

    iWorkspaceTask.dependsOn checkGitBranchTask
    iWorkspaceTask.dependsOn checkCleanWorkspaceTask

    iCheckTask.dependsOn iWorkspaceTask
    iCheckTask.dependsOn showPublishInfo

    iPublishTask.dependsOn iCheckTask

    bumpAndTagTask.dependsOn iPublishTask

    releaseTask.dependsOn bumpAndTagTask
  }

  def configureVersionAndPublications(Project project, BuildInfo buildInfo) {
    project.version = "${buildInfo.nextVersion.asText()}${buildInfo.buildSuffix}"
    project.ext.currentVersion = "${buildInfo.currentVersion.asText()}"

    configurePublications(project, buildInfo)
  }

  def configurePublications(Project project, BuildInfo buildInfo) {
    def type = buildInfo.isReleaseBuild ? 'snapshot' : 'release'
    project.ext {
      bintrayCredentials = new BintrayCredentials(project)
      artifactRepoBase = 'http://oss.jfrog.org/artifactory'
      repoPrefix = 'oss'
      releaseRepos = {
        //Only snapshots - bintray plugin takes care of non-snapshot releases
        maven {
          name 'jcenter'
          url "${artifactRepoBase}/${repoPrefix}-${type}-local"
          credentials {
            username = "${bintrayCredentials.username}"
            password = "${bintrayCredentials.password}"
          }
        }
      }
    }
  }


  static def createBuildInfo(Project project, VersioningStrategy versioningStrategy) {
    BuildInfoFactory buildInfoFactory = new BuildInfoFactory(versioningStrategy)
    buildInfoFactory.create(project)
  }

  static String releaseType(Project project) {
    project.hasProperty('releaseType') ? project.property('releaseType') : 'PATCH'
  }

  static String buildNumberFormat(Project project) {
    project.hasProperty('buildNumberFormat') ? project.property('buildNumberFormat') : '-SNAPSHOT'
  }

  static boolean dryRun(Project project) {
    project.hasProperty('dryRun') ? Boolean.valueOf(project.property('dryRun')) : false
  }
}