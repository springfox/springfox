package springfox.gradlebuild.version

trait VersioningStrategy  {
  abstract SemanticVersion current()
}
