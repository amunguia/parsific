package com.parsific;

import static com.parsific.Combinators.dropBoth;
import static com.parsific.Combinators.map;
import static com.parsific.Parsers.all;
import static com.parsific.Parsers.any;
import static com.parsific.Parsers.many;
import static com.parsific.Parsers.not;
import static com.parsific.Parsers.one;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class Text {
 
  public static <T> Either<ParserException, T> parseString(
      Parser<Character, T> parser, String string) {
    return parser.parse(new UnwindingIterator<>(new ArrayLikeString(string)));
  }

  public static Parser<Character, Character> anyOf(String anyMatch) {
    return any(toObjectArray(anyMatch));
  }

  public static <T> Parser<Character, T> clear(Parser<Character, T> parser) {
    return dropBoth(parser, whitespace(), whitespace());
  }

  public static String concat(List<Character> list) {
    return concat(list, "");
  }

  public static String concat(List<Character> list, String joiner) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
      if (i > 0) {
        builder.append(joiner);
      }
      builder.append(list.get(i).toString());
    }
    return builder.toString();
  }

  public static Parser<Character, String> digit() {
    return map(one(Character::isDigit), c -> c.toString());
  }

  public static Parser<Character, String> digits() {
    return map(many(Character::isDigit), Text::concat);
  }

  public static Parser<Character, String> letter() {
    return map(one(c -> Character.isAlphabetic(c)), c -> c.toString());
  }

  public static Parser<Character, String> letters() {
    return map(many(c -> Character.isAlphabetic(c)), Text::concat);
  }

  public static Parser<Character, String> match(String string) {
    return map(all(toObjectArray(string)), c -> string);
  }

  public static Parser<Character, String> notOneOf(String notMatch) {
    return map(not(toObjectArray(notMatch)), c -> c.toString());
  }

  public static Parser<Character, String> whitespace() {
    return map(many(Character::isWhitespace), Text::concat);
  }

  private static Character[] toObjectArray(String string) {
    char[] charArray = string.toCharArray();
    Character[] characterArray = new Character[charArray.length];
    for (int i = 0; i < charArray.length; i++) {
      characterArray[i] = charArray[i];
    }
    return characterArray;
  }
}