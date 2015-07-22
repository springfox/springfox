package springfox.gradlebuild.version

import org.gradle.api.Project
import springfox.gradlebuild.BuildInfo

interface VersioningStrategy {
  SemanticVersion current()
  void persist(Project project, BuildInfo buildInfo)
}
