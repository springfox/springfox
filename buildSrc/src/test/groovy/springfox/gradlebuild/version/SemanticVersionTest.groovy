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

import spock.lang.Specification
import springfox.gradlebuild.DirectoryBacked

class SemanticVersionTest extends Specification implements DirectoryBacked {

  def "should calculate the next version number"() {
    def semVersion = new SemanticVersion(0, 0, 0, "")
    def next = semVersion.next(releaseType)
    expect:
      next.asText() == expected
    where:
      releaseType       | expected
      ReleaseType.MAJOR | '1.0.0'
      ReleaseType.MINOR | '0.1.0'
      ReleaseType.PATCH | '0.0.1'
  }

  def "should calculate the next version number when the minor and patch versions are not zero"() {
    def semVersion = new SemanticVersion(1, 1, 1, "")
    def next = semVersion.next(releaseType)
    expect:
      next.asText() == expected
    where:
      releaseType       | expected
      ReleaseType.MAJOR | '2.0.0'
      ReleaseType.MINOR | '1.2.0'
      ReleaseType.PATCH | '1.1.2'
  }

  def "should load from a prop file"() {
    File tempDir = directory(this)
    File propFile = new File("${tempDir.absolutePath}/p.properties")
    propFile.createNewFile()
    propFile << '''
major=1
minor=1
patch=1
'''

    expect:
      def semanticVersion = new FileVersionStrategy(propFile, "-SNAPSHOT").current()
      semanticVersion.major == 1
      semanticVersion.minor == 1
      semanticVersion.patch == 1
      semanticVersion.buildSuffix == "-SNAPSHOT"
  }
}
