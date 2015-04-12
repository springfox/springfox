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
package springfox.gradlebuild.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import springfox.gradlebuild.version.BuildscriptVersionResolver
import springfox.gradlebuild.version.ReleaseType
import springfox.gradlebuild.version.SemanticVersion
import springfox.gradlebuild.version.SoftwareVersion

// git status --porcelain
class ReleaseTask extends DefaultTask {
  public static final String TASK_NAME = 'release'
  String description = 'non snapshot release flow'
  String group = 'release'

  @TaskAction
  void release() {
    def file = project.file("${project.rootDir}/version.properties")
    ReleaseType releaseType = ReleaseType.valueOf(project.property('releaseType').toUpperCase())

    SoftwareVersion releaseVersion = BuildscriptVersionResolver.projectVersion(project, SemanticVersion.get(file))
    releaseVersion.save(file)

    def xArgs = ['git',
                 'commit',
                 '-i',
                 file.absolutePath,
                 '-m',
                 "Release(${releaseType}) bumping project version to ${releaseVersion}"]

    project.exec {
      commandLine xArgs
    }

    project.exec {
      commandLine 'git', 'tag', '-a', "${releaseVersion}", '-m', "${releaseVersion}"
    }

    //git push --tags


  }
}
