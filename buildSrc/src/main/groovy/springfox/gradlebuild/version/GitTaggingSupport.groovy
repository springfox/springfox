package springfox.gradlebuild.version

import org.gradle.api.Project
import springfox.gradlebuild.BuildInfo

trait GitTaggingSupport {

  String lastAnnotatedTag() {
    def proc = "git describe --exact-match".execute();
    proc.waitFor ( );
    if ( proc.exitValue ( ) == 0 ) {
      return proc.text.trim()
    }
    proc = "git describe".execute ( );
    proc.waitFor ( );
    if ( proc.exitValue ( ) == 0 ) {
      return proc.text.trim()
    }
    return ""
  }

  def createAnnotatedTag(Project project, BuildInfo buildInfo) {
    project.logger.info("Annotating ${buildInfo.releaseType} release with tag ${buildInfo.releaseTag}")
    if (buildInfo.dryRun) {
      project.logger.warn(
          "Would have executed -> git tag -a ${buildInfo.releaseTag} -m \"Release of ${buildInfo.releaseTag}\"")
      return
    }
    project.exec {
      commandLine 'git', 'tag', '-a', "${buildInfo.releaseTag}", '-m', "Release of ${buildInfo.releaseTag}"
    }.assertNormalExitValue()
  }
}
