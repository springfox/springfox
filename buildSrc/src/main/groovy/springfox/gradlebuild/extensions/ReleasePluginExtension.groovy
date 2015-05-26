package springfox.gradlebuild.extensions

import springfox.gradlebuild.version.VersioningStrategy

class ReleasePluginExtension {
  String buildNumberFormat = "-SNAPSHOT" //uses <count> and <sha> as formats
  String releaseType = 'patch'
  boolean dryRun = false
  VersioningStrategy versionedUsing = null
}
