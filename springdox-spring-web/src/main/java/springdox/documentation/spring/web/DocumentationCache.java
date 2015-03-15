package springdox.documentation.spring.web;

import springdox.documentation.service.Documentation;

import java.util.Map;

import static com.google.common.collect.Maps.*;

public class DocumentationCache {
  private Map<String, Documentation> documentationLookup = newLinkedHashMap();

  public void addDocumentation(Documentation documentation) {
    documentationLookup.put(documentation.getGroupName(), documentation);
  }

  public Documentation documentationByGroup(String groupName) {
    return documentationLookup.get(groupName);
  }

}
