package springfox.gradlebuild.version

import org.gradle.api.Project
import springfox.gradlebuild.BuildInfo

trait GitTaggingSupport {

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
