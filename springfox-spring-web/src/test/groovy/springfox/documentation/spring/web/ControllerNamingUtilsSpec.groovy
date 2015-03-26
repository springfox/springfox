package springfox.documentation.spring.web
import com.google.common.base.Objects
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

   def "decode illegal path should return path as-is"() {
    when:
      def decoded = ControllerNamingUtils.decode(path)
    then:
      Objects.equal(decoded, path)
    where:
      path << [null, '', ""]
  }

}

