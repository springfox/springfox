package springfox.gradlebuild.extensions

import com.google.common.base.Objects
import springfox.gradlebuild.version.VersioningStrategy

class ReleasePluginExtension {
  String buildNumberFormat = "-SNAPSHOT" //uses <count> and <sha> as formats
  String releaseType = 'patch'
  boolean dryRun = false


  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("buildNumberFormat", buildNumberFormat)
        .add("releaseType", releaseType)
        .add("dryRun", dryRun)
        .toString();
  }
}
