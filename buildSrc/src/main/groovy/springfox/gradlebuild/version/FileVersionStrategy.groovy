package springfox.gradlebuild.version

class FileVersionStrategy implements VersioningStrategy {
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

}
