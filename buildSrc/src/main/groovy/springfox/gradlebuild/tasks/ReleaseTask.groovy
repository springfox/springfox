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
// git status --porcelain
class ReleaseTask extends DefaultTask {
  private static Logger LOG = Logging.getLogger(ReleaseTask.class);
  public static final String TASK_NAME = 'release'
  String description = 'non snapshot release flow'
  String group = 'release'

  @TaskAction
  void exec() {
    def buildInfo = project.rootProject.buildInfo
    LOG.info("Pushing annotated tag ${buildInfo.releaseTag}")
    if (buildInfo.dryRun) {
      project.logger.warn("[RELEASE] [DRYRUN] Would have executed -> git push origin ${buildInfo.releaseTag}")
    } else {
      project.exec {
        commandLine 'git', 'push', "origin", "${buildInfo.releaseTag}"
      }.assertNormalExitValue()
    }
  }
}
