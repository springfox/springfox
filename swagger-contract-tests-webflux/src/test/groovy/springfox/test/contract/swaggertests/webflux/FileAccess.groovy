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

package springfox.test.contract.swaggertests.webflux

import groovy.json.StringEscapeUtils
import org.codehaus.groovy.runtime.ResourceGroovyMethods

import static groovy.json.JsonOutput.*

trait FileAccess {
  String fileContents(String fileName) {
    def resource = this.getClass().getResource("$fileName")
    return ResourceGroovyMethods.getText(resource, 'UTF-8')
  }

  def maybeWriteToFile(fileName, String contents) {
    def root = System.properties.get("contract.tests.root")
    def updateContracts = System.properties.get("contract.tests.update", false)
    System.out.println("Update contracts? $updateContracts, root -> $root")
    if (updateContracts) {
      System.out.println("Writing file ${root}${File.separator}${fileName}...")
      def file
      def writer
      try {
        file = new FileOutputStream("${root}${File.separator}${fileName}")
        writer = new OutputStreamWriter(file, "UTF-8")
        writer.write(StringEscapeUtils.unescapeJava(prettyPrint(contents)))
      } catch (Exception e) {
        System.err.println("**** ERROR WRITING FILE: " + e.getMessage())
      } finally {
        writer.flush()
        writer.close()
      }
    }
    true
  }
}