package com.parsific;

public class ArrayLikeString implements ArrayLike<Character> {

  private final String string;

  public ArrayLikeString(String string) {
    this.string = string;
  }

  @Override
  public Character get(int index) {
    return string.charAt(index);
  }

  @Override
  public int length() {
    return string.length();
  }
  
}