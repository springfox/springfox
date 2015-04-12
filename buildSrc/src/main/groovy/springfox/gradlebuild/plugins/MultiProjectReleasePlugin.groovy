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
import springfox.gradlebuild.BintrayCredentials
import springfox.gradlebuild.tasks.BintrayCredentialsCheckTask
import springfox.gradlebuild.tasks.CheckCleanWorkspaceTask
import springfox.gradlebuild.tasks.CheckGitBranchTask
import springfox.gradlebuild.tasks.IntermediaryTask
import springfox.gradlebuild.tasks.ReleaseTask
import springfox.gradlebuild.tasks.SnapshotTask
import springfox.gradlebuild.version.BuildscriptVersionResolver

/**
 * Much of what this plugin does is inspired by:
 * https://www.youtube.com/watch?v=Y6SVoXFsw7I ( GradleSummit2014 - Releasing With Gradle - René Groeschke)
 *
 */
public class MultiProjectReleasePlugin implements Plugin<Project> {

  ReleaseTask releaseTask
  CheckCleanWorkspaceTask checkCleanWorkspaceTask
  SnapshotTask snapshotTask
  BintrayCredentialsCheckTask credentialCheck
  CheckGitBranchTask checkGitBranchTask

  @Override
  void apply(Project project) {
    releaseTask = project.task(ReleaseTask.TASK_NAME, type: ReleaseTask)
    snapshotTask = project.task(SnapshotTask.TASK_NAME, type: SnapshotTask)
    credentialCheck = project.task(BintrayCredentialsCheckTask.TASK_NAME, type: BintrayCredentialsCheckTask)
    checkCleanWorkspaceTask = project.task(CheckCleanWorkspaceTask.TASK_NAME, type: CheckCleanWorkspaceTask)
    checkGitBranchTask = project.task(CheckGitBranchTask.TASK_NAME, type: CheckGitBranchTask)

    configurePublications(project)
    configureSnapshotTaskGraph(project)
    configureReleaseTaskGraph(project)
  }


  def configureSnapshotTaskGraph(Project project) {
    def iSnapshotCheckTask = project.task('iSnapshotCheck', type: IntermediaryTask)

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

//  Check that the local git repository is clean and in sync with remote
//  :clean :check
//  Append 1 to the current version number (version.properties) and save the file
//  perform release to bintray
//  create a github release tag
//  push git tag and version.properties

  def configureReleaseTaskGraph(Project project) {
    def iPublishTask = project.task('iPublishTask', type: IntermediaryTask)
    def iCheckTask = project.task('iCheckTask', type: IntermediaryTask)

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

    iPublishTask.dependsOn iCheckTask
    iPublishTask.dependsOn checkCleanWorkspaceTask
    iCheckTask.mustRunAfter checkCleanWorkspaceTask

//    releaseTask.dependsOn iPublishTask
  }


  def configurePublications(Project project) {
    project.ext {
      bintrayCredentials = new BintrayCredentials(project)
      artifactRepoBase = 'http://oss.jfrog.org/artifactory'
      repoPrefix = 'oss'

      releaseRepos = {
        //Only snapshots - bintray plugin takes care of non-snapshot releases
        if (BuildscriptVersionResolver.isSnapshot(project)) {
          maven {
            name 'jcenter'
            url "${artifactRepoBase}/${repoPrefix}-${project.version.toString().endsWith('-SNAPSHOT') ? 'snapshot' : 'release'}-local"
            credentials {
              username = "${bintrayCredentials.username}"
              password = "${bintrayCredentials.password}"
            }
          }
        }
      }
    }
  }

}