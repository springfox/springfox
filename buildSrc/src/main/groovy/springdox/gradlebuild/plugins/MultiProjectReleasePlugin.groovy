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

package springdox.gradlebuild.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import springdox.gradlebuild.tasks.CheckCleanWorkspaceTask
import springdox.gradlebuild.tasks.IntermediaryTask
import springdox.gradlebuild.tasks.ReleaseTask
import springdox.gradlebuild.version.SoftwareVersion

/**
 * Plugin inspired by https://www.youtube.com/watch?v=Y6SVoXFsw7I ( GradleSummit2014 - Releasing With Gradle - René Groeschke)
 *
 */
public class MultiProjectReleasePlugin implements Plugin<Project> {

  ReleaseTask releaseTask
  CheckCleanWorkspaceTask checkCleanWorkspaceTask
  SoftwareVersion softwareVersion

  @Override
  void apply(Project project) {
    releaseTask = project.task(ReleaseTask.TASK_NAME, type: ReleaseTask)
    addTasks(project)
    configureAllProjectVersions(project)
    configureTaskDependencies(project)
  }

  def addTasks(Project project) {
    preReleaseTasks(project)
  }

  def preReleaseTasks(Project project) {
    checkCleanWorkspaceTask = project.task("checkCleanWorkspace", type: CheckCleanWorkspaceTask)
  }

  def configureAllProjectVersions(Project project) {
//    if(project.gradle.startParameter.taskNames.contains(ReleaseTask.TASK_NAME)){
//      softwareVersion = project.version.next(project.release.releaseType)
//      project.version = softwareVersion
//      project.allprojects {
//        allprojects*.version = softwareVersion
//      }
//      throw new GradleException("Version is: ${project.version}")
//    }
//    project.gradle.taskGraph.whenReady { graph ->
//      if (graph.hasTask(':release')) {
//        def nextVersion = project.version.next(project.release.releaseType)
//        project.allprojects {
//          allprojects*.version = nextVersion
//        }
//      }
//    }
  }

  def configureTaskDependencies(Project project) {
    def iCheckTask = project.task('iCheckTask', type: IntermediaryTask)
    def iPublishTask = project.task('iPublishTask', type: IntermediaryTask)

    project.afterEvaluate { evaluatedProject ->
//      iCheckTask.dependsOn evaluatedProject.getTasksByName('check', true)
      iCheckTask.dependsOn evaluatedProject.getTasksByName('build', true)

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

    releaseTask.dependsOn iPublishTask
  }
}

/**
 * gradle.addBuildListener
 * gut reset or git push
 */
