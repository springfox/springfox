/*
 *
 *  * Copyright 2019-2020 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package springfox.test.contract.oas.repository;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import java.util.TreeSet;

@NoRepositoryBean
@SuppressWarnings("unchecked")
public abstract class HashMapRepository<T, ID> implements CrudRepository<T, ID> {

  private final BeanWrapper entityBeanInfo;

  @SuppressWarnings("VisibilityModifier")
  protected final java.util.Map<ID, T> entities = new HashMap<>();

  protected HashMapRepository(Class<T> clazz) {
    entityBeanInfo = new BeanWrapperImpl(clazz);
  }

  abstract <S extends T> ID getEntityId(S entity);

  @Override
  public <S extends T> S save(S entity) {
    Assert.notNull(entity, "entity cannot be null");
    Assert.notNull(getEntityId(entity), "entity ID cannot be null");
    entities.put(getEntityId(entity), entity);
    return entity;
  }

  @Override
  public <S extends T> java.util.List<S> saveAll(Iterable<S> entities) {
    Assert.notNull(entities, "entities cannot be null");
    java.util.List<S> result = new ArrayList<>();
    entities.forEach(entity -> result.add(save(entity)));
    return result;
  }

  @Override
  public java.util.Collection<T> findAll() {
    return entities.values();
  }

  public java.util.List<T> findAll(Pageable pageable) {
    final java.util.List<T> result;
    final Sort sort = pageable.getSort();
    Comparator<T> comp = new Comparator<T>() {
      @Override
      public int compare(
          T t1,
          T t2) {
        int result = 0;
        for (Sort.Order o : sort) {
          final String prop = o.getProperty();
          java.beans.PropertyDescriptor propDesc = entityBeanInfo.getPropertyDescriptor(prop);
          result = ((Comparable<T>) propDesc.createPropertyEditor(t1).getValue())
              .compareTo((T) propDesc.createPropertyEditor(t2).getValue());
          if (o.isDescending()) {
            result = -result;
          }
          if (result != 0) {
            break;
          }
        }
        return result;
      }
    };
    final java.util.Set<T> set = new TreeSet<>(comp);
    set.addAll(entities.values());
    result = getPageSlice(pageable, set);
    return result;
  }

  private java.util.List<T> getPageSlice(
      Pageable pageable,
      java.util.Collection<T> col) {
    final ArrayList<T> all = new ArrayList<>(col);
    final int size = all.size();
    final int psize = pageable.getPageSize();
    final int pnum = pageable.getPageNumber();
    if (pnum < 1) {
      throw new IllegalArgumentException("page number must be 1 or more");
    }
    if (psize < 1) {
      throw new IllegalArgumentException("page size must be 1 or more");
    }
    // inclusive
    final int begin = (pnum - 1) * psize;
    // exclusive
    final int end = Math.min(begin + psize, size);
    if (size < begin) {
      return new ArrayList<>();
    }
    // return of slice is valid because all is local to this method
    return all.subList(begin, end);
  }

  @Override
  public long count() {
    return entities.keySet().size();
  }

  @Override
  public void delete(T entity) {
    Assert.notNull(entity, "entity cannot be null");
    deleteById(getEntityId(entity));
  }

  @Override
  public void deleteAll(Iterable<? extends T> entitiesToDelete) {
    Assert.notNull(entitiesToDelete, "entities cannot be null");
    entitiesToDelete.forEach(entity -> entities.remove(getEntityId(entity)));
  }

  @Override
  public void deleteAll() {
    entities.clear();
  }

  @Override
  public void deleteById(ID id) {
    Assert.notNull(id, "Id cannot be null");
    entities.remove(id);
  }

  @Override
  public java.util.List<T> findAllById(Iterable<ID> ids) {
    Assert.notNull(ids, "Ids cannot be null");
    java.util.List<T> result = new ArrayList<>();
    ids.forEach(id -> findById(id).ifPresent(result::add));
    return result;
  }

  @Override
  public boolean existsById(ID id) {
    Assert.notNull(id, "Id cannot be null");
    return entities.containsKey(id);
  }

  public T findOne(ID id) {
    Assert.notNull(id, "Id cannot be null");
    return entities.get(id);
  }

  @Override
  public Optional<T> findById(ID id) {
    return Optional.ofNullable(findOne(id));
  }
}
