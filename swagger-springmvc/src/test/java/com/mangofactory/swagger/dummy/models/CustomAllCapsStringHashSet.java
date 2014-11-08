//Copyright 2014 Choice Hotels International
package com.mangofactory.swagger.dummy.models;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

public class CustomAllCapsStringHashSet extends HashSet<String> {

  private static final long serialVersionUID = -5157313869620411257L;
  private static final Locale EN_LOCALE = new Locale("en_US");

  @Override
  public boolean add(final String e) {
    return super.add(e.toUpperCase(EN_LOCALE));
  }

  @Override
  public boolean addAll(final Collection<? extends String> c) {

    boolean isChanged = false;

    for (String value : c) {
      if (add(value.toUpperCase(EN_LOCALE))) {
        isChanged = true;
      }
    }
    return isChanged;
  }
}
