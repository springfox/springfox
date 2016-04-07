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

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.process.ExecResult
import spock.lang.Specification
import springfox.gradlebuild.BuildInfo
import springfox.gradlebuild.DirectoryBacked

class SemanticVersionTest extends Specification implements DirectoryBacked {

  def "should calculate the next version number"() {
    def semVersion = new SemanticVersion(0, 0, 0, "")
    def next = semVersion.next(releaseType, suffix)
    expect:
      next.asText() == expected
    where:
      releaseType       | suffix      | expected
      ReleaseType.MAJOR | ''          |'1.0.0'
      ReleaseType.MINOR | ''          |'0.1.0'
      ReleaseType.PATCH | ''          |'0.0.1'
      ReleaseType.MAJOR | '-SNAPSHOT' |'1.0.0-SNAPSHOT'
      ReleaseType.MINOR | '-SNAPSHOT' |'0.1.0-SNAPSHOT'
      ReleaseType.PATCH | '-SNAPSHOT' |'0.0.1-SNAPSHOT'
  }

  def "should calculate the next version number when the minor and patch versions are not zero"() {
    def semVersion = new SemanticVersion(1, 1, 1, "")
    def next = semVersion.next(releaseType, suffix)
    expect:
      next.asText() == expected
    where:
    releaseType       | suffix      | expected
    ReleaseType.MAJOR | ''          |'2.0.0'
    ReleaseType.MINOR | ''          |'1.2.0'
    ReleaseType.PATCH | ''          |'1.1.2'
    ReleaseType.MAJOR | '-SNAPSHOT' |'2.0.0-SNAPSHOT'
    ReleaseType.MINOR | '-SNAPSHOT' |'1.2.0-SNAPSHOT'
    ReleaseType.PATCH | '-SNAPSHOT' |'1.1.2-SNAPSHOT'
  }

  def "should load from a file"() {
    File tempDir = directory(this)
    File propFile = new File("${tempDir.absolutePath}/p.properties")
    propFile.newWriter().withWriter {
      it << '''1.1.1-SNAPSHOT'''
    }

    expect:
      def semanticVersion = new FileVersionStrategy(propFile, "-SNAPSHOT").buildVersion(ReleaseType.PATCH, false)
      semanticVersion.major == 1
      semanticVersion.minor == 1
      semanticVersion.patch == 1
      semanticVersion.buildSuffix == "-SNAPSHOT"
  }

  def "should write to a file"() {
    def current = new SemanticVersion(1, 1, 0, "")
    def version = new SemanticVersion(1, 1, 1, "-SNAPSHOT")
    File tempDir = directory(this)
    File propFile = new File("${tempDir.absolutePath}/p.properties")
    Project project = Mock(Project)
    ExecResult result = Mock(ExecResult)
    project.exec(_) >> result
    result.assertNormalExitValue() >> result
    project.logger >> Mock(Logger)
    def versionStrategy = new FileVersionStrategy(propFile, "-SNAPSHOT")
    versionStrategy.persist(
        project,
        new BuildInfo(current, version, ReleaseType.PATCH, false, false, versionStrategy))

    expect:
      def semanticVersion = versionStrategy.buildVersion(ReleaseType.PATCH, false)
      semanticVersion.major == 1
      semanticVersion.minor == 1
      semanticVersion.patch == 1
      semanticVersion.buildSuffix == "-SNAPSHOT"
  }
}
