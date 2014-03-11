package com.mangofactory.swagger.core

import org.springframework.web.util.UriUtils
import spock.lang.Specification
import spock.lang.Unroll

class ControllerNamingUtilsSpec extends Specification {

   @Unroll
   def "path roots"() {
    expect:
      //Instantiate for coverage
      ControllerNamingUtils controllerNamingUtils = new ControllerNamingUtils()
      expected == controllerNamingUtils.pathRoot(path)
      encoded == controllerNamingUtils.pathRootEncoded(path)
      expected == controllerNamingUtils.decode(encoded)
    where:
      path       | expected | encoded
      '/a/b'     | '/a'     | '/a'
      '/a/b/c/d' | '/a'     | '/a'
      'a/b'      | '/a'     | '/a'
      'a'        | '/a'     | '/a'
      '/'        | '/'      | '/'
      '//'       | '/'      | '/'
      '/{}'      | '/{}'    | '/%7B%7D'
      '/()'      | '/()'    | '/()'
   }

   def "path roots should throw exception"() {
    when:
      ControllerNamingUtils.pathRoot(path)
    then:
      thrown(IllegalArgumentException)
    where:
      path << [null, '', "", "           "]
   }
}

