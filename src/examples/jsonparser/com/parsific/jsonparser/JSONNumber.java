package com.parsific.examples.jsonparser;

public class JSONNumber implements JSONValue {

  public final double value;

  public JSONNumber(double d) {
    this.value = d;
  }

  @Override
  public Object value() {
    return value;
  }

  @Override
  public boolean isNumber() {
    return true;
  }
}