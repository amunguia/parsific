package com.parsific.examples.jsonparser;

public class JSONNull implements JSONValue {

  private static JSONNull instance = new JSONNull();

  private JSONNull() {}

  public static JSONNull instance() {
    return instance;
  }

  @Override
  public Object value() {
    return null;
  }

  @Override
  public boolean isNull() {
    return true;
  }
}