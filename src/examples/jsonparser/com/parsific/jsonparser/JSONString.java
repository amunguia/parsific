package com.parsific.examples.jsonparser;

public class JSONString implements JSONValue {

  public final String value;

  public JSONString(String s) {
    this.value = s;
  }

  @Override
  public Object value() {
    return value;
  }

  @Override
  public boolean isString() {
    return true;
  }

}