package com.parsific;

public class ArrayLikeArray<E> implements ArrayLike<E> {
  
  private final E[] array;

  public ArrayLikeArray(E[] array) {
    this.array = array;
  }

  @Override
  public E get(int index) {
    return array[index];
  }

  @Override
  public int length() {
    return array.length;
  }
}