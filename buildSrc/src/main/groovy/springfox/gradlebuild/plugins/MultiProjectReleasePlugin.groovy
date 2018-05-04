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
import springfox.gradlebuild.BuildInfo
import springfox.gradlebuild.BuildInfoFactory
import springfox.gradlebuild.tasks.BumpAndTagTask
import springfox.gradlebuild.tasks.CheckCleanWorkspaceTask
import springfox.gradlebuild.tasks.CheckGitBranchTask
import springfox.gradlebuild.tasks.IntermediaryTask
import springfox.gradlebuild.tasks.CheckRequiredSecretsTask
import springfox.gradlebuild.tasks.ReleaseTask
import springfox.gradlebuild.version.FileVersionStrategy
import springfox.gradlebuild.version.ReleaseType
import springfox.gradlebuild.version.VersioningStrategy
/**
 * Much of what this plugin does is inspired by:
 * https://www.youtube.com/watch?v=Y6SVoXFsw7I ( GradleSummit2014 - Releasing With Gradle - René Groeschke)
 *
 */
class MultiProjectReleasePlugin implements Plugin<Project> {

  ReleaseTask releaseTask
  BumpAndTagTask bumpAndTagTask
  CheckCleanWorkspaceTask checkCleanWorkspaceTask
  CheckRequiredSecretsTask credentialCheck
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
    credentialCheck = project.task(CheckRequiredSecretsTask.TASK_NAME, type: CheckRequiredSecretsTask)
    checkCleanWorkspaceTask = project.task(CheckCleanWorkspaceTask.TASK_NAME, type: CheckCleanWorkspaceTask)
    checkGitBranchTask = project.task(CheckGitBranchTask.TASK_NAME, type: CheckGitBranchTask)
    showPublishInfo = project.task('showPublishInfo') {
      group = 'Help'
      description = 'Show project publishing information'
    }

    configureVersion(project, versioningInfo)
    configureSnapshotTaskGraph(project)
    configureReleaseTaskGraph(project)
    project.tasks.showPublishInfo << {
      project.logger.lifecycle "[RELEASE] Project version: $project.version, $versioningInfo"
    }
  }

  def configureSnapshotTaskGraph(Project project) {
    def snapshotTask = project.task('snapshot', type: IntermediaryTask, group: "release")
    snapshotTask.dependsOn showPublishInfo
    snapshotTask.dependsOn checkCleanWorkspaceTask
    snapshotTask.dependsOn credentialCheck
    project.afterEvaluate { evaluatedProject ->
      def javaCheckTasks = evaluatedProject.getTasksByName('check', true)
      def artifactoryPublishTasks = evaluatedProject.getTasksByName('artifactoryPublish', true)
      snapshotTask.dependsOn javaCheckTasks
      snapshotTask.dependsOn artifactoryPublishTasks
    }
  }

  def configureReleaseTaskGraph(Project project) {
    def iCheckTask = project.task('iCheckTask', type: IntermediaryTask)
    def iPublishTask = project.task('iPublishTask', type: IntermediaryTask)

    iCheckTask.dependsOn showPublishInfo
    iCheckTask.dependsOn checkGitBranchTask
    iCheckTask.dependsOn checkCleanWorkspaceTask
    iCheckTask.dependsOn credentialCheck

    project.afterEvaluate { evaluatedProject ->
      def javaCheckTasks = evaluatedProject.getTasksByName('check', true)
      def artifactoryPublishTasks = evaluatedProject.getTasksByName('artifactoryPublish', true)
      iCheckTask.dependsOn javaCheckTasks
      iPublishTask.dependsOn artifactoryPublishTasks
    }

    iPublishTask.dependsOn iCheckTask

    bumpAndTagTask.dependsOn iPublishTask
    releaseTask.dependsOn bumpAndTagTask
  }

  def configureVersion(Project project, BuildInfo buildInfo) {
    project.version = "${buildInfo.buildVersion.asText()}"
    project.ext.buildInfo = buildInfo
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

  static boolean dryRun(Project project) {
    project.hasProperty('dryRun') ? Boolean.valueOf(project.property('dryRun')) : false
  }

}
