package com.parsific.examples.jsonparser;

import static com.parsific.Combinators.and;
import static com.parsific.Combinators.dropBoth;
import static com.parsific.Combinators.dropLeft;
import static com.parsific.Combinators.dropRight;
import static com.parsific.Combinators.map;
import static com.parsific.Combinators.maybe;
import static com.parsific.Combinators.or;
import static com.parsific.Combinators.orDefault;
import static com.parsific.Combinators.seperatedBy;
import static com.parsific.Parsers.many;
import static com.parsific.Parsers.manyParser;
import static com.parsific.Parsers.not;
import static com.parsific.Parsers.one;
import static com.parsific.Text.anyOf;
import static com.parsific.Text.clear;
import static com.parsific.Text.digits;
import static com.parsific.Text.match;

import com.parsific.Either;
import com.parsific.Parser;
import com.parsific.ParserException;
import com.parsific.Text;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class JSONParser {

  private static final Parser<Character, JSONValue> PARSER = valueParser();

  public Either<ParserException, JSONValue> parse(String json) {
    return Text.parseString(PARSER, json);
  }

  /**
   * A valid json value is a string, number, boolean, null, array or object.
   */
  private static Parser<Character, JSONValue> valueParser() {
      return or(
          clear(quotedStringParser()),
          clear(numberParser()),
          clear(map(match("false"), f -> new JSONBoolean(false))),
          clear(map(match("true"), t -> new JSONBoolean(true))),
          clear(map(match("null"), n -> JSONNull.instance())),
          clear(arrayParser()),
          clear(objectParser()));
  }

  private static Parser<Character, JSONValue> arrayParser() {
    return map(
      dropBoth(seperatedBy(delayedJSONParser(), ','), one('['), one(']')),
      list -> new JSONArray(list.toArray(new JSONValue[list.size()])));
  }

  private static Parser<Character, JSONValue> objectParser() {
    Function<LinkedList<SimpleEntry<String, JSONValue>>, JSONValue> toJSONObject =
        (list) -> {
          final Map<String, JSONValue> map = new HashMap<>();
          list.stream().forEach(
              entry -> map.put(entry.getKey(), entry.getValue()));
          return new JSONObject(map);
        };
    return map(
      dropBoth(seperatedBy(entryParser(), ','), one('{'), one('}')), toJSONObject);
  }

  private static Parser<Character, SimpleEntry<String, JSONValue>> entryParser() {
    return clear(and(
      (key, delimiter, value) -> new SimpleEntry<>((String) key.value(), value),
      quotedStringParser(), clear(one(':')), delayedJSONParser()));
  }

  private static Parser<Character, JSONValue> quotedStringParser() {
    return dropBoth(stringParser(), one('\"'), one('\"'));
  }

  private static Parser<Character, JSONValue> stringParser() {
    return map(
        manyParser(or(escapedChar(), not('\"'))),
        (list) -> new JSONString(Text.concat(list)));
  }

  private static Parser<Character, Character> escapedChar() {
    return and((a,b) -> b, one('\\'), anyOf("\"\\\b\f\n\r\t"));
  }

  private static Parser<Character, JSONValue> numberParser() {
    return and(
        JSONParser::toNumber,
        orDefault(map(anyOf("-+"), c -> c == '+'), Boolean.TRUE),
        integerParser(),
        orDefault(
            dropLeft(integerParser(), maybe(one('.'))),
            0),
        orDefault(toPowerOf10Parser(), 1.0)); 
  }

  private static Parser<Character,Integer> integerParser() {
    return map(orDefault(digits(), "0"), Integer::parseInt);
  }

  private static Parser<Character, Double> toPowerOf10Parser() {
    return and(
        JSONParser::genTo10, 
        dropLeft(
            orDefault(map(anyOf("-+"), c -> c == '+'), Boolean.TRUE),
            anyOf("eE")),
        map(digits(), Integer::parseInt));
  }

  private static JSONNumber toNumber(
      Boolean isPositive, Integer wholeNumber, Integer decimalNumber, Double to10) {
    Double doub = Double.parseDouble(wholeNumber + "." + decimalNumber);
    return new JSONNumber((isPositive ? 1 : -1) * doub * to10);
  }

  private static double genTo10(Boolean isPositive, Integer digits) {
    return Math.pow(10.0, (isPositive ? 1 : -1) * digits);
  }

  private static Parser<Character, JSONValue> delayedJSONParser() {
    return (iterator) -> PARSER.parse(iterator);
  }
}
