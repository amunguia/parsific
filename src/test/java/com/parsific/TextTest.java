package com.parsific;

import static com.parsific.Combinators.*;
import static com.parsific.Parsers.*;
import static com.parsific.Text.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.util.Arrays;

public class TextTest {

  @Test
  public void anyOf_suceedsIfAMatchIsFound() {
    assertEquals(
        new Character('d'), anyOf("abcd").parse(toIterator("d")).right());
  }

  @Test
  public void anyOf_failsIfNoMatchIsFound() {
    assertFalse(anyOf("abcd").parse(toIterator("e")).isRight());
  }

  @Test
  public void concat_joinsAListOfCharsToString() {
    assertEquals("a;b;c", concat(Arrays.asList('a', 'b', 'c'), ";"));
  }

  @Test
  public void concat_worksWithEmptyList() {
    assertEquals("", concat(Arrays.asList()));
  }

  @Test
  public void digit_parsesADigit() {
    assertEquals("1", digit().parse(toIterator("1")).right());
  }

  @Test
  public void digits_parsesAllDigits() {
    assertEquals(
      "1234567890", digits().parse(toIterator("1234567890abc")).right());
  }

  @Test
  public void letter_parsesALetter() {
    assertEquals("a", letter().parse(toIterator("a")).right());
  }

  @Test
  public void letters_parsesAllLetters() {
    assertEquals(
      "abcd", letters().parse(toIterator("abcd123")).right());
  }

  @Test
  public void match_returnsMatchedString() {
    assertEquals(
      "match", match("match").parse(toIterator("match text")).right());
  }

  @Test
  public void match_failsIfNotExactMatch() {
    assertFalse(match("match").parse(toIterator("matcc text")).isRight());
  }

  @Test
  public void notOneOf_succeedsOnFailedMatch() {
    assertEquals("d", notOneOf("abc").parse(toIterator("d")).right());
  }

  @Test
  public void notOneOf_failsOnMach() {
    assertFalse(notOneOf("abc").parse(toIterator("c")).isRight());
  }

  @Test
  public void whitespace_returnsAllWhitespace() {
    assertEquals("\n\t\n", whitespace().parse(toIterator("\n\t\nabc")).right());
  }

  private UnwindingIterator<Character> toIterator(String string) {
    return new UnwindingIterator<>(new ArrayLikeString(string));
  }
}