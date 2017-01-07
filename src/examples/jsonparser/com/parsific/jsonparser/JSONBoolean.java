package com.parsific.examples.jsonparser;

public class JSONBoolean implements JSONValue {

  public final boolean value;

  public JSONBoolean(boolean b) {
    this.value = b;
  }

  @Override
  public Object value() {
    return value;
  }

  @Override
  public boolean isBoolean() {
    return true;
  }
}