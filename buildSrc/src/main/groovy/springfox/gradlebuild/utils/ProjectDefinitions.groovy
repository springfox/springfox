/*
 *
 *
 *
 *
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
 *
 *
 */

package springfox.gradlebuild.utils

import org.gradle.api.Project

class ProjectDefinitions {

  static final TEST_PROJECTS = [
      'swagger-contract-tests',
      'swagger-contract-tests-webflux',
      'oas-contract-tests',
      'buildSrc',
      'springfox-spring-config',
      'springfox-petstore',
      'springfox-petstore-webflux'
  ]

  static publishables(Project project) {
    return project.subprojects.findAll {
      !TEST_PROJECTS.contains(it.name)
    }
  }
  static publishable(Project project) {
    !TEST_PROJECTS.contains(project.name)
  }
}
