package com.parsific.examples.jsonparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.parsific.Either;
import com.parsific.ParserException;

import org.junit.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

public class JSONParserTest {

  private static final JSONParser parser = new JSONParser();

  @Test
  public void testBoolean_true() {
    assertJSONEquals(Boolean.TRUE, "true");
  }

  @Test
  public void testBoolean_false() {
    assertJSONEquals(Boolean.FALSE, "false");
  }

  @Test
  public void testNull() {
    assertJSONEquals(null, "null");
  }

  @Test
  public void testNumber_success() {
    assertJSONEquals(new Double(1), "1");
    assertJSONEquals(new Double(-1), "-1");
    assertJSONEquals(new Double(1.2345), "1.2345");
    assertJSONEquals(new Double(0.5), "500e-3");
    assertJSONEquals(new Double(12345), "12345");
    assertJSONEquals(new Double(0.12345), "0.12345");
  }

  @Test
  public void testString() {
    String actual = "\"This \n\t is a 'test' \\\"string\\\".\"";
    String expected = "This \n\t is a 'test' \"string\".";
    assertJSONEquals(expected, actual);
  }

  @Test
  public void testArray() {
    // Also tests that the parser clears whitespace.
    JSONValue[] value = (JSONValue[]) parser.parse(
        "[12345 ,true , false , [  1.2 , 0.55 ], \n\t \"string\"  ]").right().value();

    assertEquals(new Double(12345), value[0].value());
    assertEquals(Boolean.TRUE, value[1].value());
    assertEquals(Boolean.FALSE, value[2].value());
    assertEquals("string", value[4].value());

    JSONValue[] innerArray = (JSONValue[]) value[3].value();
    assertEquals(new Double(1.2), innerArray[0].value());
    assertEquals(new Double(0.55), innerArray[1].value());
  }

  @Test
  public void testObject() throws Exception {
    Map object = (Map) parser.parse(
      "{   \"key1\"  :  1, \n   \"key2\" \n\t :   2     }").right().value();
    assertTrue(object.containsKey("key1"));
    assertTrue(object.containsKey("key2"));
    assertEquals(new Double(1.0), ((JSONValue) object.get("key1")).value());
    assertEquals(new Double(2.0), ((JSONValue) object.get("key2")).value());
  }

  private void assertJSONEquals(Object expected, String json) {
    Either<ParserException, JSONValue> parseResult = parser.parse(json);
    assertTrue(parseResult.isRight());
    assertEquals(expected, parseResult.right().value());
  }
}