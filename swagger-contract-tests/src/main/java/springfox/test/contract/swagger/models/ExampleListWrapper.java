package springfox.test.contract.swagger.models;

import java.util.List;

//From stack overflow answer https://stackoverflow.com/a/10518009
public class ExampleListWrapper {
  private List<Example> exampleList;

  public List<Example> getExampleList() {
    return exampleList;
  }

  public void setExampleList(List<Example> exampleList) {
    this.exampleList = exampleList;
  }
}
