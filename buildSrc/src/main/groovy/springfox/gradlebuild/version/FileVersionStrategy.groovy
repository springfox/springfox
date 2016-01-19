package springfox.gradlebuild.version

import org.gradle.api.Project
import springfox.gradlebuild.BuildInfo

class FileVersionStrategy implements VersioningStrategy {
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
          patch.toInteger(),
          buildNumberSuffix)
    }
    version

  }

  @Override
  void persist(Project project, BuildInfo buildInfo) {
    def commitChangesCommand = "git commit -i '${versionFile.absolutePath}' -m 'Release(${buildInfo.nextVersion}) " +
      "tagging project with tag ${buildInfo.releaseTag}'"
    project.logger.info("Saving ${buildInfo.nextVersion.asText()} to the version file (${versionFile.absolutePath})")
    if (buildInfo.dryRun) {
      project.logger.info("Will execute command: $commitChangesCommand")
      return
    }
    versionFile.withOutputStream {
      it.write("${buildInfo.nextVersion.major}.${buildInfo.nextVersion.minor}.${buildInfo.nextVersion.patch}$buildNumberSuffix".bytes)
    }

    def proc = commitChangesCommand.execute();
    proc.waitFor();
    if (proc.exitValue() != 0) {
      project.logger.error("Unable to save the file and commit changes to repo!")
    }
  }
}
