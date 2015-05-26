package springfox.gradlebuild.version

import org.gradle.api.Project

class FileVersionStrategy implements VersioningStrategy {
  private final Project project

  FileVersionStrategy(Project project) {
    this.project = project
  }

  @Override
  SemanticVersion current() {
    def versionFile = project.file("${project.rootDir}/version.properties")
    def props = new Properties()
    versionFile.withInputStream() { stream ->
      props.load(stream)
    }
    new SemanticVersion(toInt(props.major), toInt(props.minor), toInt(props.patch))
  }
}
