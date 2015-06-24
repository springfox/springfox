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
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction
import springfox.gradlebuild.BuildInfo
import springfox.gradlebuild.version.VersioningStrategy

class BumpAndTagTask extends DefaultTask {
  private static Logger LOG = Logging.getLogger(BumpAndTagTask.class);
  public static final String TASK_NAME = 'bumpAndTag'
  String description = 'Bumps the version file and tags the release'
  String group = 'release'
  BuildInfo buildInfo
  VersioningStrategy versioning

  @TaskAction
  void exec() {
    LOG.info("Bumping the version and tagging after release using ($versioning.class.simpleName)")
    versioning.persist(buildInfo)
  }
}
