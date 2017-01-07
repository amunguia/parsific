package com.parsific.examples.jsonparser;

import java.util.Map;

public class JSONObject implements JSONValue {

  private final Map<String, JSONValue> map;

  public JSONObject(Map<String, JSONValue> map) {
    this.map = map;
  }

  @Override
  public Object value() {
    return map;
  }

  @Override
  public boolean isObject() {
    return true;
  }
}