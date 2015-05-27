package springfox.gradlebuild.version

import springfox.gradlebuild.BuildInfo

interface VersioningStrategy {
  SemanticVersion current()
  void persist(BuildInfo buildInfo)
}
