package springfox.gradlebuild

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import springfox.gradlebuild.version.ReleaseType
import springfox.gradlebuild.version.SemanticVersion
import springfox.gradlebuild.version.VersioningStrategy

import static springfox.gradlebuild.plugins.MultiProjectReleasePlugin.*

class BuildInfoFactory {
  private static Logger LOG = Logging.getLogger(BuildInfoFactory.class);
  VersioningStrategy versioningStrategy

  BuildInfoFactory(VersioningStrategy versioningStrategy) {
    this.versioningStrategy = versioningStrategy
  }

  BuildInfo create(Project project) {
    ReleaseType releaseType = releaseType(project)
    boolean dryRun = dryRun(project)
    def isReleaseBuild = project.gradle.startParameter.taskNames.contains("release")

    SemanticVersion buildVersion = versioningStrategy.buildVersion(releaseType, isReleaseBuild)
    LOG.debug("current verison: ${versioningStrategy.current()}, verbuild version: $buildVersion, dryRun: $dryRun, releaseBuild: $isReleaseBuild")
    new BuildInfo(
        versioningStrategy.current(),
        buildVersion,
        releaseType,
        isReleaseBuild,
        dryRun,
        versioningStrategy)
  }


}
