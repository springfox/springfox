/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package springfox.documentation.schema;

import java.util.Objects;

public class Xml {
  private String name;
  private String namespace;
  private String prefix;
  private Boolean attribute;
  private Boolean wrapped;

  public Xml name(String name) {
    this.setName(name);
    return this;
  }

  public Xml namespace(String namespace) {
    this.setNamespace(namespace);
    return this;
  }

  public Xml prefix(String prefix) {
    this.setPrefix(prefix);
    return this;
  }

  public Xml attribute(Boolean attribute) {
    this.setAttribute(attribute);
    return this;
  }

  public Xml wrapped(Boolean wrapped) {
    this.setWrapped(wrapped);
    return this;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNamespace() {
    return this.namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getPrefix() {
    return this.prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public Boolean getAttribute() {
    return this.attribute;
  }

  public void setAttribute(Boolean attribute) {
    this.attribute = attribute;
  }

  public Boolean getWrapped() {
    return this.wrapped;
  }

  public void setWrapped(Boolean wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Xml xml = (Xml) o;
    return Objects.equals(name, xml.name) &&
        Objects.equals(namespace, xml.namespace) &&
        Objects.equals(prefix, xml.prefix) &&
        Objects.equals(attribute, xml.attribute) &&
        Objects.equals(wrapped, xml.wrapped);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, namespace, prefix, attribute, wrapped);
  }
}
