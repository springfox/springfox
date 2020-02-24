package springfox.documentation.spring.web.dummy.models;

import java.util.List;

public class Bar {

  private Integer id;

  private String name;

  private List<Foo> foos;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Foo> getFoos() {
    return foos;
  }

  public void setFoos(List<Foo> foos) {
    this.foos = foos;
  }

}
