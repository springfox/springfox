package springfox.documentation.service.model

import java.util.List;

import spock.lang.Specification;
import springfox.documentation.service.AllowableListValues
import springfox.documentation.service.AllowableRangeValues

class AllowableListValuesSpec extends Specification {
  def "Bean properties test" () {
    given:
      def sut = new AllowableListValues([ 'First', "Second"], "First allowable list")
      sut.values.add("Third")
      sut.values.add("Test")
      sut.values.add("Begin")
    expect:
      sut.values.size == 5
      sut.values.get(4) == "Begin"
      sut.valueType == "First allowable list"
    }
  
  def "Class .equals() and .hashCode() test" () {
    given:
      def sut = new AllowableListValues([ 'First', "Second"], "First allowable list")
      def sutTest = new AllowableListValues(values, valueType)
    expect:
      sut.equals(sutTest) == expectedEquality
      sut.equals(sut)
      !sut.equals(null)
      !sut.equals(new Object())
    and:
      (sut.hashCode() == sutTest.hashCode()) == expectedEquality
      sut.hashCode() == sut.hashCode()
    where:
      values                    | valueType               | expectedEquality  
      [ 'Third']                | "Second allowable list" | false
      [ '1', "2","3"]           | "Third allowable list"  | false
      [ 'Green', "Red","White"] | "First allowable list"  | false
      [ 'Green', "Red","White"] |  "First allowable list" | false
      [ 'First', "Second"]      | "First allowable list"  | true
      [ 'First', "Second"]      | "Second allowable list" | false
    }
}
