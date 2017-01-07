package com.parsific;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Stack;

public final class UnwindingIterator<E> implements PeekingIterator<E> {

  private final ArrayLike<E> arrayLike;
  private int next;
  private final Stack<Integer> unwindStack;

  public UnwindingIterator(ArrayLike<E> arrayLike) {
    this.arrayLike = arrayLike;
    this.next = 0;
    this.unwindStack = new Stack<>();
  }

  public int nextIndex() {
    return next;
  }

  @Override
  public boolean hasNext() {
    return next < arrayLike.length();
  }

  @Override
  public E next() {
    if (hasNext()) {
      return arrayLike.get(next++);
    }
    throw new NoSuchElementException("Reached end of iterator.");
  }

  @Override
  public E peek() {
    if (hasNext()) {
      return arrayLike.get(next);
    }
    throw new NoSuchElementException("Reached end of iterator.");
  }

  public UnwindingIterator<E> clearWind() {
    if (!unwindStack.isEmpty()) {
      unwindStack.pop();
    }
    return this;
  }

  public UnwindingIterator<E> unwind() {
    if (!unwindStack.isEmpty()) {
      next = unwindStack.pop();
    }
    return this;
  }

  /**
   * For every call to wind, there should be a corresponding call to either
   * unwind or clearWind.
   */
  public UnwindingIterator<E> wind() {
    unwindStack.push(next);
    return this;
  }
}