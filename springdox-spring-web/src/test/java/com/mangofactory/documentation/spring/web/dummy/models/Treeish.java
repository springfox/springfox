package com.mangofactory.documentation.spring.web.dummy.models;

public class Treeish implements java.io.Serializable {
  private Treeish example;
  private String treeishField;

  public Treeish getExample() {
    return example;
  }

  public void setExample(Treeish example) {
    this.example = example;
  }

  public Treeish(Treeish example) {
    this.example = example;
  }

  public String getTreeishField() {
    return treeishField;
  }

  public void setTreeishField(String treeishField) {
    this.treeishField = treeishField;
  }
}
