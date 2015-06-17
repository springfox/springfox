package springfox.gradlebuild
import com.google.common.base.MoreObjects
import com.google.common.base.Strings
import springfox.gradlebuild.version.SemanticVersion

class BuildInfo {
  SemanticVersion currentVersion
  SemanticVersion nextVersion
  String releaseType
  String releaseTag
  boolean dryRun
  private final String buildSuffix

  BuildInfo(SemanticVersion currentVersion, SemanticVersion nextVersion, String releaseType, String releaseTag,
            boolean dryRun, String buildSuffix) {
    this.buildSuffix = buildSuffix
    this.dryRun = dryRun
    this.currentVersion = currentVersion
    this.nextVersion = nextVersion
    this.releaseType = releaseType
    this.releaseTag = releaseTag
  }

  boolean getDryRun() {
    return dryRun
  }

  SemanticVersion getCurrentVersion() {
    return currentVersion
  }

  SemanticVersion getNextVersion() {
    return nextVersion
  }

  String getReleaseType() {
    return releaseType
  }

  String getReleaseTag() {
    return releaseTag
  }

  String getBuildSuffix() {
    return buildSuffix
  }

  boolean getIsReleaseBuild() {
    Strings.isNullOrEmpty(buildSuffix)
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("currentVersion", currentVersion)
        .add("nextVersion", nextVersion)
        .add("releaseType", releaseType)
        .add("releaseTag", releaseTag)
        .add("dryRun", dryRun)
        .add("buildSuffix", buildSuffix)
        .toString();
  }
}
