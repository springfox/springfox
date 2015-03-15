package springdox.documentation.schema.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEvent;

public class ObjectMapperConfigured extends ApplicationEvent {
  private final ObjectMapper objectMapper;

  /**
   * Create a new ApplicationEvent.
   *
   * @param source the component that published the event (never {@code null})
   */
  public ObjectMapperConfigured(Object source, ObjectMapper objectMapper) {
    super(source);
    this.source = source;
    this.objectMapper = objectMapper;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
