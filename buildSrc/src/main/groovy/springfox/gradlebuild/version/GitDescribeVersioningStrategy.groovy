package springfox.gradlebuild.version

import org.gradle.api.Project
import springfox.gradlebuild.BuildInfo

class GitDescribeVersioningStrategy implements VersioningStrategy, GitVersionParser, GitTaggingSupport  {

  private final String buildNumberFormat

  GitDescribeVersioningStrategy(String buildNumberFormat) {
    this.buildNumberFormat = buildNumberFormat
  }

  @Override
  SemanticVersion current() {
    def proc = "git describe --exact-match".execute();
    proc.waitFor();
    if (proc.exitValue() == 0) {
      parseTransform(proc.text.trim(), buildNumberFormat)
    }
    proc = "git describe".execute();
    proc.waitFor();
    if (proc.exitValue() == 0) {
      return parseTransform(proc.text.trim(), buildNumberFormat)
    }
    return new SemanticVersion(0, 0, 0, "")
  }

  @Override
  void persist(Project project, BuildInfo buildInfo) {
    createAnnotatedTag(project, buildInfo)
  }

  static VersioningStrategy create(String buildNumberFormat) {
    return new GitDescribeVersioningStrategy(buildNumberFormat)
  }

}
