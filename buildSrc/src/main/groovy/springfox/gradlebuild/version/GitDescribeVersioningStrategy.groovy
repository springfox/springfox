package springfox.gradlebuild.version

class GitDescribeVersioningStrategy implements VersioningStrategy  {
  private String buildNumberSuffix

  private GitDescribeVersioningStrategy(String buildNumberSuffix) {
    this.buildNumberSuffix = buildNumberSuffix
  }

  @Override
  SemanticVersion current() {
    def proc = "git describe --exact-match".execute();
    proc.waitFor();
    if (proc.exitValue() == 0) {
      parseTransform(proc.text.trim(), buildNumberSuffix)
    }
    proc = "git describe".execute();
    proc.waitFor();
    if (proc.exitValue() == 0) {
      return parseTransform(proc.text.trim(), buildNumberSuffix)
    }
    return new SemanticVersion(0, 0, 0)
  }

  static VersioningStrategy create(String buildSuffix) {
    return new GitDescribeVersioningStrategy(buildSuffix)
  }

}
