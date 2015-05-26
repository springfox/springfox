package springfox.gradlebuild.version

trait VersioningStrategy extends GitVersionParser {
  abstract SemanticVersion current()
}
