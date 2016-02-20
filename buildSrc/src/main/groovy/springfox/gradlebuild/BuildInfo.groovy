package springfox.gradlebuild

import com.google.common.base.MoreObjects
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
    if (isReleaseBuild) {
      buildVersion
    } else {
      def nextRelease = versioningStrategy.nextVersion(buildVersion, releaseType, isReleaseBuild)
      new SemanticVersion(nextRelease.major, nextRelease.minor, nextRelease.patch, "")
    }
  }

  boolean getIsReleaseBuild() {
    isReleaseBuild
  }

  VersioningStrategy getVersioningStrategy() {
    versioningStrategy
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("releaseVersion", currentVersion)
        .add("buildVersion", buildVersion)
        .add("nextVersion", nextVersion)
        .add("releaseType", releaseType)
        .add("releaseTag", releaseTag)
        .add("dryRun", dryRun)
        .toString();
  }
}
