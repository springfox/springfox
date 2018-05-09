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

package springfox.gradlebuild.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static springfox.gradlebuild.plugins.MultiProjectReleasePlugin.dryRun

class CheckGitBranchTask extends DefaultTask {

  public static final String TASK_NAME = "checkGitBranchTask"
  String description = "Checks the current branch is master in sync with remote"
  String group = "release"

  @TaskAction
  void check() {
    String requiredBranch = "master"
    if (dryRun(project)) {
      project.logger.warn("[RELEASE] [DRYRUN] Would have checked the branch is master!")
      return
    }
    project.exec {
      commandLine "git", "fetch"
    }

    def sout = new ByteArrayOutputStream()
    project.exec {
      commandLine "git", "rev-parse", "--abbrev-ref", "HEAD"
      standardOutput = sout
    }
    def branch = sout.toString()
    assert branch.trim() == requiredBranch: "Incorrect release branch: ${branch}. You must be on ${requiredBranch} to release"

    sout = new ByteArrayOutputStream()
    project.exec {
      commandLine "git", "status", "-sb"
      standardOutput = sout
    }
    def gitStatus = sout.toString()
    assert !gitStatus.contains('['): "The local branch is not in sync with remote"
  }
}
