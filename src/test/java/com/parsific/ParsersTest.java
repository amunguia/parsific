package com.parsific;

import static com.parsific.Parsers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ParsersTest {

  @Test
  public void all_succeedsWhenAllFound() {
    Parser<Character, List<Character>> allP = all('a', 'b', 'c');
    List<Character> parsedChars = allP.parse(toIterator("abc")).right();

    assertEquals(3, parsedChars.size());
    assertEquals(new Character('a'), parsedChars.get(0));
    assertEquals(new Character('b'), parsedChars.get(1));
    assertEquals(new Character('c'), parsedChars.get(2));
  }

  @Test
  public void all_failsWhenAllNotFound() {
    Parser<Character, List<Character>> allP = all('a', 'b', 'c');
    assertFalse(allP.parse(toIterator("abd")).isRight());
  }

  @Test
  public void all_returnsEmptyListWhenNoElementsMatched() {
    Parser<Character, List<Character>> allP = all();
    assertTrue(allP.parse(toIterator("abd")).right().isEmpty());
  }

  @Test
  public void any_returnsMatch() {
    Parser<Character, Character> anyP = any('a', 'b', 'c', 'd');
    UnwindingIterator<Character> text = toIterator("ad");
    assertEquals(new Character('a'), anyP.parse(text).right());
    assertEquals(new Character('d'), anyP.parse(text).right());
  }

  @Test
  public void any_failsOnNoMatch() {
    Parser<Character, Character> anyP = any('a', 'b', 'c', 'd');
    assertFalse(anyP.parse(toIterator("A")).isRight());
  }

  @Test
  public void many_succeedsOnNoMatches() {
    Parser<Character, List<Character>> manyP =
        many((c) -> c == 'a' || c == 'b');
    assertTrue(manyP.parse(toIterator("ccc")).right().isEmpty());
  }

  @Test
  public void many_returnsAllMatches() {
    Parser<Character, List<Character>> manyP =
        many((c) -> c == 'a' || c == 'b' || c == 'c');
    UnwindingIterator<Character> text = toIterator("abcd");
    List<Character> parsedChars = manyP.parse(text).right();

    assertEquals(3, parsedChars.size());
    assertEquals(new Character('a'), parsedChars.get(0));
    assertEquals(new Character('b'), parsedChars.get(1));
    assertEquals(new Character('c'), parsedChars.get(2));
  }

  @Test
  public void many1_returnsAllMatches() {
    Parser<Character, List<Character>> many1P =
        many1((c) -> c == 'a' || c == 'b' || c == 'c');
    UnwindingIterator<Character> text = toIterator("abcd");
    List<Character> parsedChars = many1P.parse(text).right();

    assertEquals(3, parsedChars.size());
    assertEquals(new Character('a'), parsedChars.get(0));
    assertEquals(new Character('b'), parsedChars.get(1));
    assertEquals(new Character('c'), parsedChars.get(2));
  }

  @Test
  public void many1_succeedsOnOneMatch() {
    Parser<Character, List<Character>> many1P =
        many1((c) -> c == 'a' || c == 'b' || c == 'c');
    UnwindingIterator<Character> text = toIterator("ad");
    List<Character> parsedChars = many1P.parse(text).right();

    assertEquals(1, parsedChars.size());
    assertEquals(new Character('a'), parsedChars.get(0));
  }

  @Test
  public void many1_failsOnZeroMatches() {
    Parser<Character, List<Character>> many1P =
        many1((c) -> c == 'a' || c == 'b');
    assertFalse(many1P.parse(toIterator("ccc")).isRight());
  }

  @Test
  public void not_returnsOnNotEquals() {
    Parser<Character, Character> notP = not('a');
    assertEquals(new Character('b'), notP.parse(toIterator("b")).right());
  }

  @Test
  public void not_worksWithMultiArg() {
    Parser<Character, Character> notP = not ('a', 'b', 'c');
    assertEquals(new Character('d'), notP.parse(toIterator("d")).right());
  }

  @Test
  public void not_failsOnEquals() {
    Parser<Character, Character> notP = not('a');
    assertFalse(notP.parse(toIterator("a")).isRight());
  }

  @Test
  public void one_returnsMatch() {
    Parser<Character, Character> oneP = one();
    assertEquals(new Character('a'), oneP.parse(toIterator("a")).right());
  }

  @Test
  public void one_failsAtEndOfString() {
    Parser<Character, Character> oneP = one();
    Either<String, Character> result = oneP.parse(new ArrayLikeString(""));
    assertFalse(result.isRight());
  }

  @Test
  public void one_predicateSuccess() {
    Parser<Character, Character> oneP = one(c -> c == 'a');
    assertEquals(new Character('a'), oneP.parse(toIterator("a")).right());
  }

  @Test
  public void one_predicateFails() {
    Parser<Character, Character> oneP = one(c -> c == 'a');
    assertFalse(oneP.parse(toIterator("b")).isRight());
  }

  @Test
  public void one_exactMatchSuccess() {
    Parser<Character, Character> oneP = one('a');
    assertEquals(new Character('a'), oneP.parse(toIterator("a")).right());
  }

  @Test
  public void one_exactMatchFails() {
    Parser<Character, Character> oneP = one('a');
    assertFalse(oneP.parse(toIterator("b")).isRight());
  }

  @Test
  public void one_updatesIterator() {
    Parser<Character, Character> oneP = one();
    UnwindingIterator<Character> text = toIterator("a");
    Either<String, Character> result = oneP.parse(text);
    assertTrue(result.isRight());
    result = oneP.parse(text);
    assertFalse(result.isRight());
  }

  private UnwindingIterator<Character> toIterator(String string) {
    return new UnwindingIterator<>(new ArrayLikeString(string));
  }
}