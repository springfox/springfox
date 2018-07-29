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
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class CheckRequiredSecretsTask extends DefaultTask {
  public static final TASK_NAME = "checkRequiredSecrets"
  String description = 'verifies credentials for bintray/github/oss-sonatype'
  String group = 'release'

  @TaskAction
  def action() {
    requiredProperty('bintrayUsername', 'BINTRAY_USER_NAME')
    requiredProperty('bintrayPassword', 'BINTRAY_PASSWORD')
    requiredProperty('githubToken', 'GITHUB_TOKEN')
    requiredProperty('sonatypeUsername', 'SONATYPE_USER_NAME')
    requiredProperty('sonatypePassword', 'SONATYPE_PASSWORD')
  }

  String requiredProperty(String propName, String environmentVariable) {
    def value = project.hasProperty(propName) ?
        project.property(propName) :
        System.getenv(environmentVariable)
    if (!(value == null ? true : value.isEmpty())) {
      return value
    }
    throw new GradleException("Either gradle property: $propName or environment variable: $environmentVariable" +
        " must not present!")
  }
}
