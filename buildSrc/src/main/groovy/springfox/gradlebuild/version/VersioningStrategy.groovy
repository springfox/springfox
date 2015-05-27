package springfox.gradlebuild.version

interface VersioningStrategy {
  SemanticVersion current()
}
