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

package springdox.gradlebuild.version

import spock.lang.Specification
import springdox.gradlebuild.DirectoryBacked

class SnapshotVersionTest extends Specification implements DirectoryBacked {

  def "snapshot should decorate a sem version"() {
    File propFile = new File("${directory(this).absolutePath}/p.properties")
    propFile.createNewFile()
    propFile << '''
major=1
minor=1
patch=1
'''
    expect:
      def semanticVersion = SemanticVersion.get(propFile)
      def snapshotVersion = new SnapshotVersion(semanticVersion)
      snapshotVersion.major == 1
      snapshotVersion.minor == 1
      snapshotVersion.patch == 1
      snapshotVersion.asText() == '1.1.1-SNAPSHOT'
      snapshotVersion.toString() == '1.1.1-SNAPSHOT'
  }

  def "next version should be the current version"() {
    def version = new SnapshotVersion(new SemanticVersion(0, 0, 0))
    def next = version.next(ReleaseType.MAJOR)
    expect:
      next.asText() == '0.0.0-SNAPSHOT'
      next.toString() == '0.0.0-SNAPSHOT'
  }
}