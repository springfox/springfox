package springfox.gradlebuild

import springfox.gradlebuild.version.ReleaseType
import springfox.gradlebuild.version.SemanticVersion
import springfox.gradlebuild.version.VersioningStrategy

class BuildInfo {
  private final SemanticVersion buildVersion
  private final ReleaseType releaseType
  private final boolean dryRun
  private final VersioningStrategy versioningStrategy
  private final boolean isReleaseBuild
  private final SemanticVersion currentVersion

  BuildInfo(
      SemanticVersion currentVersion,
      SemanticVersion buildVersion,
      ReleaseType releaseType,
      boolean isReleaseBuild,
      boolean dryRun,
      VersioningStrategy versioningStrategy) {

    this.currentVersion = currentVersion
    this.buildVersion = buildVersion
    this.isReleaseBuild = isReleaseBuild
    this.dryRun = dryRun
    this.releaseType = releaseType
    this.versioningStrategy = versioningStrategy
  }

  boolean getDryRun() {
    return dryRun
  }

  SemanticVersion getCurrentVersion() {
    return currentVersion
  }

  SemanticVersion getBuildVersion() {
    buildVersion
  }

  SemanticVersion getNextVersion() {
    versioningStrategy.nextVersion(buildVersion, releaseType, isReleaseBuild)
  }

  String getReleaseType() {
    releaseType
  }

  String getReleaseTag() {
    return "${buildVersion.major}.${buildVersion.minor}.${buildVersion.patch}"
  }

  boolean getIsReleaseBuild() {
    isReleaseBuild
  }

  VersioningStrategy getVersioningStrategy() {
    versioningStrategy
  }

  @Override
  public String toString() {
    return new StringBuffer(this.getClass().getSimpleName())
        .append("{")
        .append("releaseVersion=").append(currentVersion).append(", ")
        .append("buildVersion=").append(buildVersion).append(", ")
        .append("nextVersion=").append(nextVersion).append(", ")
        .append("releaseType=").append(releaseType).append(", ")
        .append("releaseTag=").append(releaseTag).append(", ")
        .append("dryRun=").append(dryRun)
        .append("}").toString();
  }
}
