/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
package springfox.test.contract.swagger.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class Bug1749 {
  @NotNull
  @Min(value = 1, message = "must be > 1")
  private Integer pageNumber = 1;
  @NotNull
  @Max(value = 50, message = "must be < 50")
  private Integer pageSize = 20;
  private SortDirection sortDirection;
  private String sortField;

  public Bug1749(
      int pageNumber,
      int pageSize) {
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
  }

  public Bug1749(
      int pageNumber,
      int pageSize,
      String sortField) {
    this(
        pageNumber,
        pageSize);
    this.sortField = sortField;
  }

  public Bug1749(
      int pageNumber,
      int pageSize,
      String sortField,
      SortDirection sortDirection) {
    this(
        pageNumber,
        pageSize,
        sortField);
    this.sortDirection = sortDirection;
  }

  public Map<String, String> toMap() {
    Map<String, String> map = new HashMap<String, String>();
    map.put(
        "pageNumber",
        Integer.toString(pageNumber));
    map.put(
        "pageSize",
        Integer.toString(pageSize));
    if (sortDirection != null) {
      map.put(
          "sortDirection",
          sortDirection.toString());
    }
    if (sortField != null) {
      map.put(
          "sortField",
          sortField);
    }
    return map;
  }

  // region Auto-generated code
  public Bug1749() {
  }

  @JsonIgnore
  public boolean isAscending() {
    return sortDirection == SortDirection.ASC;
  }

  @JsonIgnore
  public boolean isSet() {
    return pageNumber != null && pageSize != null;
  }

  public Bug1749 setSort(
      String field,
      SortDirection direction) {
    this.sortField = field;
    this.sortDirection = direction;
    return this;
  }

  public Integer getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public SortDirection getSortDirection() {
    return sortDirection;
  }

  public void setSortDirection(SortDirection sortDirection) {
    this.sortDirection = sortDirection;
  }

  public String getSortField() {
    return sortField;
  }

  public void setSortField(String sortField) {
    this.sortField = sortField;
  }
  // endregion
}