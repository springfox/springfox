package com.mangofactory.documentation.spring.web.readers.operation;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.mangofactory.documentation.service.model.Operation;

public class OperationPositionalOrdering extends Ordering<Operation> {
  @Override
  public int compare(Operation first, Operation second) {
    return Ints.compare(first.getPosition(), second.getPosition());
  }
}
