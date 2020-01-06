package springfox.documentation.schema;

public class JacksonXmlPresentInClassPathCondition extends ClassPresentInClassPathCondition {
  @Override
  protected String getClassName() {
    return "com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty";
  }
}
