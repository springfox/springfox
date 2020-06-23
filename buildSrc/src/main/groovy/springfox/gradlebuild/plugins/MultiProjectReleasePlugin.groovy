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
  IntermediaryTask checkWorkspaceTask

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
    checkWorkspaceTask = project.task('checkWorkspace', type: IntermediaryTask)

    showPublishInfo = project.task('showPublishInfo') {
      group = 'Help'
      description = 'Show project publishing information'
      doLast {
        project.logger.lifecycle "[RELEASE] Project version: $project.version, $versioningInfo"
      }
    }

    configureVersion(project, versioningInfo)
    configureGlobalTasks()
    configureSnapshotTaskGraph(project)
    configureReleaseTaskGraph(project)
  }

  def configureGlobalTasks() {
    checkWorkspaceTask.dependsOn showPublishInfo
    checkWorkspaceTask.dependsOn checkCleanWorkspaceTask
    checkWorkspaceTask.dependsOn credentialCheck
  }

  def configureSnapshotTaskGraph(Project project) {
    def publishSnapshot = project.task('publishSnapshot', type: IntermediaryTask, group: "release")

    publishSnapshot.dependsOn checkWorkspaceTask

    project.afterEvaluate { evaluatedProject ->
      def javaCheckTasks = evaluatedProject.getTasksByName('check', true)
      def artifactoryPublishTasks = evaluatedProject.getTasksByName('artifactoryPublish', true)
      publishSnapshot.dependsOn javaCheckTasks
      publishSnapshot.dependsOn artifactoryPublishTasks
    }
  }

  def configureReleaseTaskGraph(Project project) {
    def publishTask = project.task('publishRelease', type: IntermediaryTask)

    publishTask.dependsOn checkWorkspaceTask
    publishTask.dependsOn checkGitBranchTask

    project.afterEvaluate { evaluatedProject ->
      def javaCheckTasks = evaluatedProject.getTasksByName('check', true)
      def bintrayUploadTasks = evaluatedProject.getTasksByName('bintrayUpload', true)
      publishTask.dependsOn javaCheckTasks
      publishTask.dependsOn bintrayUploadTasks
    }
    bumpAndTagTask.dependsOn publishTask
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
