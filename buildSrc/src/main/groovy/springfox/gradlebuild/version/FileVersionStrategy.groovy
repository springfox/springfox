package springfox.gradlebuild.version

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import springfox.gradlebuild.BuildInfo

class FileVersionStrategy implements VersioningStrategy {
  private static Logger LOG = Logging.getLogger(FileVersionStrategy.class);
  private final File versionFile
  private final String buildNumberSuffix

  FileVersionStrategy(File versionFile, String buildNumberSuffix) {
    this.buildNumberSuffix = buildNumberSuffix
    this.versionFile = versionFile
  }

  @Override
  SemanticVersion current() {
    def props = new Properties()
    versionFile.withInputStream() { stream ->
      props.load(stream)
    }
    new SemanticVersion(props.major.toInteger(), props.minor.toInteger(), props.patch.toInteger(), buildNumberSuffix)
  }

  @Override
  void persist(Project project, BuildInfo buildInfo) {
    def commitChangesCommand = "git commit -i '${versionFile.absolutePath}' -m 'Release(${buildInfo.nextVersion}) " +
      "tagging project with tag ${buildInfo.releaseTag}'"
    LOG.info("Saving $buildInfo.nextVersion.asText() to the version file ($versionFile.absolutePath)")
    if (buildInfo.dryRun) {
      LOG.info("Will execute command: $commitChangesCommand")
      return
    }
    def properties = new Properties()
    properties.major = "${buildInfo.nextVersion.major}".toString()
    properties.minor = "${buildInfo.nextVersion.minor}".toString()
    properties.patch = "${buildInfo.nextVersion.patch}".toString()
    properties.store(versionFile.newWriter(), null)

    def proc = commitChangesCommand.execute();
    proc.waitFor();
    if (proc.exitValue() != 0) {
      LOG.error("Unable to save the file and commit changes to repo!")
    }
  }
}
