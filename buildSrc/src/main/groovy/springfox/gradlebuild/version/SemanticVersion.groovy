/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package springfox.gradlebuild.version

class SemanticVersion implements SoftwareVersion {
  int major, minor, patch
  private final String buildSuffix

  SemanticVersion(int major, int minor, int patch, String buildSuffix) {
    this.buildSuffix = buildSuffix
    this.major = major
    this.minor = minor
    this.patch = patch
  }

  SemanticVersion next(ReleaseType releaseType, String buildSuffix) {
    if (releaseType == ReleaseType.MAJOR) {
      new SemanticVersion(major + 1, 0, 0, buildSuffix)
    } else if (releaseType == ReleaseType.MINOR) {
      new SemanticVersion(major, minor + 1, 0, buildSuffix)
    } else if (releaseType == ReleaseType.PATCH) {
      new SemanticVersion(major, minor, patch + 1, buildSuffix)
    }
  }

  public String asText() {
    "${major}.${minor}.${patch}$buildSuffix"
  }

  @Override
  String getBuildSuffix() {
    buildSuffix
  }

  @Override
  String toString() {
    return asText()
  }

}
