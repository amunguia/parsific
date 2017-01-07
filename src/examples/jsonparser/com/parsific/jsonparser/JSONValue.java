package com.parsific.examples.jsonparser;

import java.util.Map;

public interface JSONValue {

  Object value();

  default boolean isArray() {
    return false;
  }

  default boolean isBoolean() {
    return false;
  }

  default boolean isNull() {
    return false;
  }

  default boolean isNumber() {
    return false;
  }

  default boolean isObject() {
    return false;
  }

  default boolean isString() {
    return false;
  }

  default JSONValue[] getArray() {
    return isArray() ? (JSONValue[]) value() : null;
  }

  default Boolean getBoolean() {
    return isBoolean() ? (Boolean) value() : null;
  }

  default Double getNumber() {
    return isNumber() ? (Double) value() : null;
  }

  default Map<String, JSONValue> getObject() {
    return isObject() ? (Map<String, JSONValue>) value() : null;
  }

  default String getString() {
    return isString() ? (String) value() : null;
  }
  
}