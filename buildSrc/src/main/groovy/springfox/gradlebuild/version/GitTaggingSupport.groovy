package springfox.gradlebuild.version

import org.gradle.api.Project
import springfox.gradlebuild.BuildInfo

trait GitTaggingSupport {

  String lastAnnotatedTag(Project project) {
    def proc = "git -C ${project.getRootDir().toString()} describe --exact-match".execute();
    proc.waitFor();
    if (proc.exitValue() == 0) {
      return proc.text.trim()
    }
    proc = "git -C ${project.getRootDir().toString()} describe".execute()
    proc.waitFor()
    if (proc.exitValue() == 0) {
      return proc.text.trim()
    }
    return "2.9.3"
  }

  def createAnnotatedTag(Project project, BuildInfo buildInfo) {
    project.logger.lifecycle("[RELEASE] Annotating ${buildInfo.releaseType} release with tag ${buildInfo.releaseTag}")
    def tagCommand = "git tag -a ${buildInfo.releaseTag} -m 'Release of ${buildInfo.releaseTag}'"
    if (buildInfo.dryRun) {
      project.logger.warn(
          "[RELEASE] [DRYRUN] Would have executed -> $tagCommand")
      return
    }
    def proc = tagCommand.execute()
    proc.waitFor()
    def err = new StringBuilder()
    def out = new StringBuilder()
    proc.consumeProcessOutput(out, err)
    proc.waitFor()
    if (proc.exitValue() != 0) {
      project.logger.error("[RELEASE] Unable to save the file and commit changes to repo!")
      project.logger.error("[ERROR] $err")
    } else {
      project.logger.lifecycle("[RELEASE] $out")
    }
  }
}
