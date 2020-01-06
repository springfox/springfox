package springfox.gradlebuild

import org.gradle.api.Project
import springfox.gradlebuild.version.ReleaseType
import springfox.gradlebuild.version.SemanticVersion
import springfox.gradlebuild.version.VersioningStrategy

import static springfox.gradlebuild.plugins.MultiProjectReleasePlugin.*

class BuildInfoFactory {
  VersioningStrategy versioningStrategy

  BuildInfoFactory(VersioningStrategy versioningStrategy) {
    this.versioningStrategy = versioningStrategy
  }

  BuildInfo create(Project project) {
    ReleaseType releaseType = releaseType(project)
    boolean dryRun = dryRun(project)
    def isReleaseBuild = project.gradle.startParameter.taskNames.contains("release")

    SemanticVersion buildVersion = versioningStrategy.buildVersion(releaseType, isReleaseBuild)
    project.logger.lifecycle("[RELEASE] current version: ${versioningStrategy.current(project)}, " +
        "build version: $buildVersion, dryRun: $dryRun, releaseBuild: $isReleaseBuild")
    new BuildInfo(
        versioningStrategy.current(project),
        buildVersion,
        releaseType,
        isReleaseBuild,
        dryRun,
        versioningStrategy)
  }


}
