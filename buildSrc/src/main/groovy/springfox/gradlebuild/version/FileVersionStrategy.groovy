package springfox.gradlebuild.version

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
    def props = new Properties()
    versionFile.withInputStream() { stream ->
      props.load(stream)
    }
    new SemanticVersion(props.major.toInteger(), props.minor.toInteger(), props.patch.toInteger(), buildNumberSuffix)
  }

  @Override
  void persist(BuildInfo buildInfo) {
    def properties = new Properties()
    properties.major = "${buildInfo.nextVersion.major}".toString()
    properties.minor = "${buildInfo.nextVersion.minor}".toString()
    properties.patch = "${buildInfo.nextVersion.patch}".toString()
    properties.store(versionFile.newWriter(), null)
  }
}
