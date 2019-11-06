package springfox.gradlebuild.version

import org.gradle.api.Project
import springfox.gradlebuild.BuildInfo

interface VersioningStrategy {
  SemanticVersion buildVersion(ReleaseType releaseType, boolean isReleaseBuild)
  SemanticVersion nextVersion(SemanticVersion buildVersion, ReleaseType releaseType, boolean isReleaseBuild)
  SemanticVersion current(Project project)
  void persist(Project project, BuildInfo buildInfo)

}
