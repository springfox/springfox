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

class BintrayCredentialsCheckTask extends DefaultTask {
  public static final TASK_NAME = "bintrayCredentialsCheck"
  String description = 'verifies bintray credentials'
  String group = 'release'

  @TaskAction
  def action() {
    requiredProperty('bintrayUsername')
    requiredProperty('bintrayPassword')
  }

  String requiredProperty(String propName) {
    if (project.hasProperty(propName)) {
      assert project.property(propName): "Property ${propName} must not be blank!"
      return project.property(propName)
    } else {
      throw new IllegalArgumentException("Property ${propName} is required!")
    }
  }
}
