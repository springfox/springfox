package springfox.gradlebuild.version

import org.gradle.api.Project
import springfox.gradlebuild.BuildInfo

class GitDescribeVersioningStrategy implements VersioningStrategy, GitVersionParser, GitTaggingSupport  {

  private final String buildNumberFormat

  GitDescribeVersioningStrategy(String buildNumberFormat) {
    this.buildNumberFormat = buildNumberFormat
  }

  @Override
  SemanticVersion buildVersion(ReleaseType releaseType, boolean isReleaseBuild) {
    current().next(releaseType, buildNumberFormat)
  }

  @Override
  SemanticVersion current(Project project) {
    parseTransform(lastAnnotatedTag(project), buildNumberFormat)
  }

  @Override
  void persist(Project project, BuildInfo buildInfo) {
    createAnnotatedTag(project, buildInfo)
  }

  static VersioningStrategy create(String buildNumberFormat) {
    return new GitDescribeVersioningStrategy(buildNumberFormat)
  }

  @Override
  SemanticVersion nextVersion(SemanticVersion buildVersion, ReleaseType releaseType, boolean isReleaseBuild) {
    buildVersion.next(releaseType, buildNumberFormat)
  }
}
