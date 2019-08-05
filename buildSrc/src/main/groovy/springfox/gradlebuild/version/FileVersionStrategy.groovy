package springfox.gradlebuild.version

import org.gradle.api.Project
import springfox.gradlebuild.BuildInfo

class FileVersionStrategy implements VersioningStrategy, GitTaggingSupport, GitVersionParser {
  private final File versionFile
  private final String buildNumberSuffix

  FileVersionStrategy(File versionFile, String buildNumberSuffix) {
    this.buildNumberSuffix = buildNumberSuffix
    this.versionFile = versionFile
  }

  @Override
  SemanticVersion buildVersion(ReleaseType releaseType, boolean isReleaseBuild) {
    SemanticVersion version
    versionFile.withInputStream() { stream ->

      def versionLine = stream.readLines().first()
      def (major, minor, patch) = versionLine.replace(buildNumberSuffix, "").split("\\.")
      version = new SemanticVersion(
          major.toInteger(),
          minor.toInteger(),
          patch.toInteger(),
          isReleaseBuild ? "" : buildNumberSuffix)
    }
    version
  }

  @Override
  SemanticVersion current(Project project) {
    parseTransform(lastAnnotatedTag(project), "")
  }

  @Override
  SemanticVersion nextVersion(SemanticVersion buildVersion, ReleaseType releaseType, boolean isReleaseBuild) {
    buildVersion.next(releaseType, isReleaseBuild ? buildNumberSuffix : "")
  }

  @Override
  void persist(Project project, BuildInfo buildInfo) {
    if (buildInfo.isReleaseBuild) {
      createAnnotatedTag(project, buildInfo)
      updateVersionFile(project, buildInfo)
      commitToRepository(project, buildInfo)
    } else {
      project.logger.warn("[RELEASE] Should never be called when its a snapshot build")
    }
  }

  def commitToRepository(Project project, BuildInfo buildInfo) {
    def commitChanges = """git commit -am \
 'Preparing for next version ${buildInfo.nextVersion}.'"""
    if (buildInfo.dryRun) {
      project.logger.warn("[RELEASE] [DRYRUN] Will execute command: $commitChanges")
      return
    }
    def proc = commitChanges.execute()
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

  def updateVersionFile(project, buildInfo) {
    if (buildInfo.dryRun) {
      project.logger.warn("[RELEASE] [DRYRUN] Would have saved ${buildInfo.nextVersion.asText()} " +
          "to the version file (${versionFile.absolutePath})")
      return
    }
    versionFile.withOutputStream {
      it.write("${buildInfo.nextVersion.asText()}".bytes)
    }
  }
}
