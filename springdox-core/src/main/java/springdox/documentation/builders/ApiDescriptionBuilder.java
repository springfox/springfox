package springdox.documentation.builders;

import com.google.common.collect.Ordering;
import springdox.documentation.service.ApiDescription;
import springdox.documentation.service.Operation;

import java.util.List;

public class ApiDescriptionBuilder {
  private String path;
  private String description;
  private List<Operation> operations;
  private Ordering<Operation> operationOrdering;
  private Boolean hidden;

  public ApiDescriptionBuilder(Ordering<Operation> operationOrdering) {
    this.operationOrdering = operationOrdering;
  }

  public ApiDescriptionBuilder path(String path) {
    this.path = BuilderDefaults.defaultIfAbsent(path, this.path);
    return this;
  }

  public ApiDescriptionBuilder description(String description) {
    this.description = BuilderDefaults.defaultIfAbsent(description, this.description);
    return this;
  }

  public ApiDescriptionBuilder operations(List<Operation> operations) {
    if (operations != null) {
      this.operations = operationOrdering.sortedCopy(operations);
    }
    return this;
  }

  public ApiDescriptionBuilder hidden(boolean hidden) {
    this.hidden = hidden;
    return this;
  }

  public ApiDescription build() {
    return new ApiDescription(path, description, operations, hidden);
  }
}