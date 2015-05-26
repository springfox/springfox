package springfox.gradlebuild.version

import org.gradle.api.Project

class FileVersionStrategy implements VersioningStrategy {
  private final File versionFile

  FileVersionStrategy(File versionFile) {
    this.versionFile = versionFile
  }

  @Override
  SemanticVersion current() {
    def props = new Properties()
    versionFile.withInputStream() { stream ->
      props.load(stream)
    }
    new SemanticVersion(props.major.toInteger(), props.minor.toInteger(), props.patch.toInteger())
  }
}
