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
import springfox.gradlebuild.BintrayCredentials
import springfox.gradlebuild.BuildInfo
import springfox.gradlebuild.BuildInfoFactory
import springfox.gradlebuild.tasks.*
import springfox.gradlebuild.version.FileVersionStrategy
import springfox.gradlebuild.version.ReleaseType
import springfox.gradlebuild.version.VersioningStrategy
/**
 * Much of what this plugin does is inspired by:
 * https://www.youtube.com/watch?v=Y6SVoXFsw7I ( GradleSummit2014 - Releasing With Gradle - René Groeschke)
 *
 */
public class MultiProjectReleasePlugin implements Plugin<Project> {

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
    versioningStrategy = new FileVersionStrategy(
        new File("${project.projectDir}/.version"),
        buildNumberFormat(project))
    BuildInfo versioningInfo = createBuildInfo(project, versioningStrategy)
    releaseTask = project.task(ReleaseTask.TASK_NAME, type: ReleaseTask)
    bumpAndTagTask = project.task(BumpAndTagTask.TASK_NAME, type: BumpAndTagTask)
    snapshotTask = project.task(SnapshotTask.TASK_NAME, type: SnapshotTask)
    credentialCheck = project.task(BintrayCredentialsCheckTask.TASK_NAME, type: BintrayCredentialsCheckTask)
    checkCleanWorkspaceTask = project.task(CheckCleanWorkspaceTask.TASK_NAME, type: CheckCleanWorkspaceTask)
    checkGitBranchTask = project.task(CheckGitBranchTask.TASK_NAME, type: CheckGitBranchTask)
    showPublishInfo = project.task('showPublishInfo') {
      group = 'Help'
      description = 'Show project publishing information'
    }

    configureVersionAndPublications(project, versioningInfo)
    configureSnapshotTaskGraph(project)
    configureReleaseTaskGraph(project)
    project.tasks.showPublishInfo << {
      project.logger.info "======= Project version: $project.version, $versioningInfo"
    }
  }

  def configureSnapshotTaskGraph(Project project) {
    def iSnapshotCheckTask = project.task('iSnapshotCheck', type: IntermediaryTask)
    iSnapshotCheckTask.dependsOn showPublishInfo
    project.afterEvaluate { evaluatedProject ->
      def javaCheckTasks = evaluatedProject.getTasksByName('check', true)
      iSnapshotCheckTask.dependsOn javaCheckTasks

      evaluatedProject.subprojects.each { p ->
        p.tasks.findByPath('publish').each { t ->
          snapshotTask.dependsOn(t)
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

      evaluatedProject.subprojects.each { p ->
        p.tasks.findByPath('bintrayUpload').each { t ->
          iPublishTask.dependsOn(t)
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
    project.version = "${buildInfo.buildVersion.asText()}"
    project.ext.buildInfo = buildInfo

    configurePublications(project, buildInfo)
  }

  def configurePublications(Project project, BuildInfo buildInfo) {
    def isSnapshotBuild = isSnapshotBuild(project)
    def type = isSnapshotBuild ? 'snapshot' : 'release'
    def login = new BintrayCredentials(project)
    def artifactRepoBase = 'http://oss.jfrog.org/artifactory'
    def repoPrefix = 'oss'
    project.ext {
      bintrayCredentials = login
      releaseRepos = {
        //Only snapshots - bintray plugin takes care of non-snapshot releases
        if (isSnapshotBuild) {
          project.logger.info("Setting up maven repo for snapshot build: $buildInfo")
          maven {
            name 'jfrogOss'
            url "${artifactRepoBase}/${repoPrefix}-${type}-local"
            credentials {
              username = "${login.username}"
              password = "${login.password}"
            }
          }
        }
      }
    }
  }

  static boolean isSnapshotBuild(Project project) {
    project.gradle.startParameter.taskNames.contains("snapshot")
  }

  static def createBuildInfo(Project project, VersioningStrategy versioningStrategy) {
    BuildInfoFactory buildInfoFactory = new BuildInfoFactory(versioningStrategy)
    buildInfoFactory.create(project)
  }

  static ReleaseType releaseType(Project project) {
    project.hasProperty('releaseType') ? ReleaseType.valueOf(project.property('releaseType')) : ReleaseType.PATCH
  }

  static String buildNumberFormat(Project project) {
    project.hasProperty('buildNumberFormat') ? project.property('buildNumberFormat') : '-SNAPSHOT'
  }

  public static boolean dryRun(Project project) {
    project.hasProperty('dryRun') ? Boolean.valueOf(project.property('dryRun')) : false
  }

}
