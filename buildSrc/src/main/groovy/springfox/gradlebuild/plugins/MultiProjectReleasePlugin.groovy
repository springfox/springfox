/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package springfox.gradlebuild.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import springfox.gradlebuild.BintrayCredentials
import springfox.gradlebuild.tasks.BintrayCredentialsCheckTask
import springfox.gradlebuild.tasks.CheckCleanWorkspaceTask
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

  @Override
  void apply(Project project) {
    releaseTask = project.task(ReleaseTask.TASK_NAME, type: ReleaseTask)
    snapshotTask = project.task(SnapshotTask.TASK_NAME, type: SnapshotTask)
    credentialCheck = project.task(BintrayCredentialsCheckTask.TASK_NAME, type: BintrayCredentialsCheckTask)

    configurePublications(project)
    addTasks(project)
    configureTaskDependencies(project)
  }

  def addTasks(Project project) {
    preReleaseTasks(project)
  }

  def preReleaseTasks(Project project) {
    checkCleanWorkspaceTask = project.task(CheckCleanWorkspaceTask.TASK_NAME, type: CheckCleanWorkspaceTask)
  }

  def configureTaskDependencies(Project project) {
    def iCheckTask = project.task('iCheckTask', type: IntermediaryTask)
    def iPublishTask = project.task('iPublishTask', type: IntermediaryTask)
    def iSnapshotCheckTask = project.task('iSnapshotCheck', type: IntermediaryTask)

    project.afterEvaluate { evaluatedProject ->
      def javaCheckTasks = evaluatedProject.getTasksByName('check', true)
      iCheckTask.dependsOn javaCheckTasks
      iSnapshotCheckTask.dependsOn javaCheckTasks

      evaluatedProject.subprojects.each {
        def bintrayPublishTask = it.tasks.findByPath('bintrayUpload')
        if (bintrayPublishTask) {
          iPublishTask.dependsOn(bintrayPublishTask)
        }

        def jcenterTasks = it.tasks.findAll { it.name.contains('ToJcenterRepository') }
        if (jcenterTasks) {
          snapshotTask.dependsOn jcenterTasks
        }
      }
    }

    iPublishTask.dependsOn iCheckTask
    iPublishTask.dependsOn checkCleanWorkspaceTask
    iCheckTask.mustRunAfter checkCleanWorkspaceTask

    releaseTask.dependsOn iPublishTask

    //Snapshot dependency graph
    snapshotTask.dependsOn iSnapshotCheckTask
    snapshotTask.dependsOn checkCleanWorkspaceTask
    snapshotTask.dependsOn credentialCheck
    iSnapshotCheckTask.mustRunAfter checkCleanWorkspaceTask
    iSnapshotCheckTask.mustRunAfter credentialCheck

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