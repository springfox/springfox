/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package springfox.gradlebuild.version

class SemanticVersion implements SoftwareVersion {
  int major, minor, patch

  SemanticVersion(int major, int minor, int patch) {
    this.major = major
    this.minor = minor
    this.patch = patch
  }

  static SemanticVersion get(File propFile) {
    def props = new Properties()
    propFile.withInputStream() { stream ->
      props.load(stream)
    }
    new SemanticVersion(toInt(props.major), toInt(props.minor), toInt(props.patch))
  }

  void save(File propFile) {
    def properties = new Properties()
    properties.major = "${major}".toString()
    properties.minor = "${minor}".toString()
    properties.patch = "${patch}".toString()
    properties.store(propFile.newWriter(), null)
  }

  SemanticVersion next(ReleaseType releaseType) {
    if (releaseType == ReleaseType.MAJOR) {
      new SemanticVersion(major + 1, minor, patch)
    } else if (releaseType == ReleaseType.MINOR) {
      new SemanticVersion(major, minor + 1, patch)
    } else if (releaseType == ReleaseType.PATCH) {
      new SemanticVersion(major, minor, patch + 1)
    }
  }

  public String asText() {
    "${major}.${minor}.${patch}"
  }

  @Override
  String toString() {
    return asText()
  }

  private static toInt(String s) {
    Integer.parseInt(s)
  }
}
