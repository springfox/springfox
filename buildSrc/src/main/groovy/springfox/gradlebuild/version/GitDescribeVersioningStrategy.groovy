package springfox.gradlebuild.version

class GitDescribeVersioningStrategy implements VersioningStrategy, GitVersionParser  {

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



  static VersioningStrategy create(buildNumberFormat) {
    return new GitDescribeVersioningStrategy(buildNumberFormat)
  }

  @Override
  String buildNumber() {
    throw new UnsupportedOperationException()
  }
}
