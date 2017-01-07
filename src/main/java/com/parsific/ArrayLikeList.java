package com.parsific;

import java.util.List;

public class ArrayLikeList<E> implements ArrayLike<E> {

  private final List<E> list;

  public ArrayLikeList(List<E> list) {
    this.list = list;
  }

  @Override
  public E get(int index) {
    return list.get(index);
  }

  @Override
  public int length() {
    return list.size();
  }
}