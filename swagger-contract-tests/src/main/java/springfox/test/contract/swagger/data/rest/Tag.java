package springfox.test.contract.swagger.data.rest;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  private String name;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "person_tag",
      joinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id",
          referencedColumnName = "id"))
  private List<Person> people;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Person> getPeople() {
    return people;
  }

  public void setPeople(List<Person> people) {
    this.people = people;
  }
}
