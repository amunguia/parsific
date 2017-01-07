package com.parsific.examples.jsonparser;

public class JSONArray implements JSONValue {

  public final JSONValue[] value;

  public JSONArray(JSONValue[] a) {
    this.value = a;
  }

  @Override
  public Object value() {
    return value;
  }

  @Override
  public boolean isArray() {
    return true;
  }
}