package com.parsific;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Stack;

public final class UnwindingIterator<E> implements Iterator<E> {

  private int next;
  private final Stack<Integer> unwindStack;
  private final List<E> list;

  public UnwindingIterator(Collection<E> collection) {
    this.next = 0;
    this.unwindStack = new Stack<>();
    this.list = new ArrayList<>(collection);
  }

  @Override
  public boolean hasNext() {
    return next < list.size();
  }

  @Override
  public E next() {
    if (hasNext()) {
      return list.get(next++);
    }
    throw new NoSuchElementException("Reached end of iterator.");
  }

  public UnwindingIterator<E> unwind() {
    if (!unwindStack.isEmpty()) {
      next = unwindStack.pop();
    }
    return this;
  }

  public UnwindingIterator<E> wind() {
    unwindStack.push(next);
    return this;
  }
}