package springfox.gradlebuild.version

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import springfox.gradlebuild.BuildInfo

class GitDescribeVersioningStrategy implements VersioningStrategy, GitVersionParser  {

  private static Logger LOG = Logging.getLogger(GitDescribeVersioningStrategy.class);
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
  void persist(BuildInfo buildInfo) {
    def command = "git tag -a ${buildInfo.releaseTag} -m \"Release(${buildInfo.nextVersion}) tagging project with tag ${buildInfo.releaseTag}\""
    if (buildInfo.dryRun) {
      LOG.info("Would have executed: $command")
      return
    }
    def proc = command.execute();
    proc.waitFor();
    if (proc.exitValue() == 0) {
      LOG.info("Successfully executed: $command")
    }
  }

  static VersioningStrategy create(String buildNumberFormat) {
    return new GitDescribeVersioningStrategy(buildNumberFormat)
  }

}
