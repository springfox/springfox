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
package springfox.test.contract.swagger.models;

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
