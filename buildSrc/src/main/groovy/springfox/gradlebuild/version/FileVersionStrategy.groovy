package springfox.gradlebuild.version

import org.gradle.api.Project
import springfox.gradlebuild.BuildInfo

class FileVersionStrategy implements VersioningStrategy, GitTaggingSupport {


  private final File versionFile
  private final String buildNumberSuffix

  FileVersionStrategy(File versionFile, String buildNumberSuffix) {
    this.buildNumberSuffix = buildNumberSuffix
    this.versionFile = versionFile
  }

  @Override
  SemanticVersion current() {
    SemanticVersion version
    versionFile.withInputStream() { stream ->

      def versionLine = stream.readLines().first()
      def (major, minor, patch) = versionLine.replace(buildNumberSuffix, "").split("\\.")
      version = new SemanticVersion(
          major.toInteger(),
          minor.toInteger(),
          patch.toInteger() - 1,
          buildNumberSuffix)
    }
    version

  }

  @Override
  void persist(Project project, BuildInfo buildInfo) {
    updateVersionFile(project, buildInfo)
    commitToRepository(project, buildInfo)
    createAnnotatedTag(project, buildInfo)
  }

  def commitToRepository(Project project, buildInfo) {
    def commitChanges = """git commit -i '${versionFile.absolutePath}' \
-m 'Releasing version (${buildInfo.nextVersion}) tagging project with tag ${buildInfo.releaseTag}'"""
    if (buildInfo.dryRun) {
      project.logger.warn("Will execute command: $commitChanges")
      return
    }
    def proc = commitChanges.execute();
    proc.waitFor();
    if (proc.exitValue() != 0) {
      project.logger.error("Unable to save the file and commit changes to repo!")
    }
  }

  def updateVersionFile(project, buildInfo) {
    if (buildInfo.dryRun) {
      project.logger.warn(
          "Would have saved ${buildInfo.nextVersion.asText()} to the version file (${versionFile.absolutePath})")
      return
    }
    versionFile.withOutputStream {
      it.write("${buildInfo.nextVersion.major}.${buildInfo.nextVersion.minor}.${buildInfo.nextVersion.patch}$buildNumberSuffix".bytes)
    }
  }
}
