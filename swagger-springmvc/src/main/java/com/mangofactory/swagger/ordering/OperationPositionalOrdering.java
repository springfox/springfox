package com.mangofactory.swagger.ordering;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.mangofactory.swagger.models.dto.Operation;

public class OperationPositionalOrdering extends Ordering<Operation> {
  @Override
  public int compare(Operation first, Operation second) {
    return Ints.compare(first.getPosition(), second.getPosition());
  }
}
