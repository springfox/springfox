import spock.lang.Specification
import spock.lang.Unroll

class HelloSpockTest extends Specification {
  @Unroll("#name, #length")
  def "length of Spock's and his friends' names"() {
    expect:
    name.size() == length

    where:
    name     | length
    "Spock"  | 5
    "Kirk"   | 4
    "Scotty" | 6
  }
}