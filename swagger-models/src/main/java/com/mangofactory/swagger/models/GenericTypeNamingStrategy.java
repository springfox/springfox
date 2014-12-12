package com.mangofactory.swagger.models;

/**
 * Strategy to provide the strings used while naming generic types in the swagger output
 */
public interface GenericTypeNamingStrategy {
  /**
   * @return the string used to denote the beginning of a generic i.e. the &lt; in List&lt;String&gt;
   */
  public String getOpenGeneric();
  /**
   * @return the string used to denote the beginning of a generic i.e. the &gt; in List&lt;String&gt;
   */
  public String getCloseGeneric();
  /**
   * @return the string used to denote the beginning of a generic i.e. the , in Map&lt;String,String&gt;
   */
  public String getTypeListDelimiter();
}
