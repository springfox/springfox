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

import static springfox.gradlebuild.plugins.MultiProjectReleasePlugin.*

class CheckCleanWorkspaceTask extends DefaultTask {
  public static final String TASK_NAME = "checkCleanWorkspace"
  String description = "Checks workspace is clean"
  String group = "release"

  @TaskAction
  void check() {
    if (dryRun(project)) {
      project.logger.warn("[RELEASE] [DRYRUN] Would have checked the workspace is clean!")
      return
    }
    def sout = new ByteArrayOutputStream()
    project.exec {
      commandLine "git", "status", "--porcelain"
      standardOutput = sout
    }
    def gitStatus = sout.toString()
    assert gitStatus == "": "Workspace is not clean ${gitStatus}"
  }
}
