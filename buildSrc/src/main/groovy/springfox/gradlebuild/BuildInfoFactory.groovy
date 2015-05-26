package springfox.gradlebuild

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import springfox.gradlebuild.version.ReleaseType
import springfox.gradlebuild.version.SemanticVersion
import springfox.gradlebuild.version.VersioningStrategy

class BuildInfoFactory {
  private static Logger LOG = Logging.getLogger(BuildInfoFactory.class);
  VersioningStrategy versioningStrategy

  BuildInfoFactory(VersioningStrategy versioningStrategy) {
    this.versioningStrategy = versioningStrategy
  }

  BuildInfo create(Project project) {
    String releaseType = project.release.releaseType
    boolean dryRun = project.release.dryRun
    def isReleaseBuild = project.gradle.startParameter.taskNames.contains("release")
    SemanticVersion currentVersion = versioningStrategy.current()
    SemanticVersion nextVersion = currentVersion.next(ReleaseType.valueOf(releaseType.toUpperCase()))
    String releaseTag = nextVersion.asText()
    LOG.info("Current version: $currentVersion, Next version: $nextVersion")
    def buildSuffix = isReleaseBuild ? "" : currentVersion.buildSuffix
    new BuildInfo(currentVersion, nextVersion, releaseType, releaseTag, dryRun, buildSuffix)
  }


}
