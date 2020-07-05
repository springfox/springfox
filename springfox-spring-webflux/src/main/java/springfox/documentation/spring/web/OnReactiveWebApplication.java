package springfox.documentation.spring.web;

import springfox.documentation.common.ClassPresentInClassPathCondition;

public class OnReactiveWebApplication extends ClassPresentInClassPathCondition {
  private static final String REACTIVE_WEB_APPLICATION_CLASS = "org.springframework.web.reactive.HandlerResult";

  @Override
  protected String getClassName() {
    return REACTIVE_WEB_APPLICATION_CLASS;
  }
}
