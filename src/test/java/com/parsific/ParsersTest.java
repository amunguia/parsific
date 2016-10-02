package com.parsific;

import static com.parsific.Parsers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ParsersTest {

  private UnwindingIterator<Character> alphabeticText;
  private UnwindingIterator<Character> digitText;
  private UnwindingIterator<Character> emptyText;
  private UnwindingIterator<Character> manyAText;

  @Before
  public void setUp() {
    alphabeticText = new UnwindingIterator<>(new ArrayLikeString("a"));
    manyAText = new UnwindingIterator<>(new ArrayLikeString("aaabbb"));
    digitText = new UnwindingIterator<>(new ArrayLikeString("1"));
    emptyText = new UnwindingIterator<>(new ArrayLikeString(""));
  }

  @Test
  public void many_emptyListOnFail() {
    Parser<Character, List<Character>> manyAParser = many((c) -> c == 'b');
    Either<String, List<Character>> result = manyAParser.parse(digitText);
    assertTrue(result.isRight());
    assertEquals(0, result.right().size());
  }

  @Test
  public void many_parsesAllMatches() {
    Parser<Character, List<Character>> manyAParser = many((c) -> c == 'a');
    Either<String, List<Character>> result = manyAParser.parse(manyAText);
    assertTrue(result.isRight());
    assertEquals(3, result.right().size());
  }

  @Test
  public void many_withTransform_emptyListOnFail() {
    Parser<Character, Object[]> manyAParser =
        many(
            (Character c) -> false,
            (List<Character> list) ->
                list.stream().map((Character c) -> 'A').toArray());
    Either<String, Object[]> result = manyAParser.parse(digitText);
    assertTrue(result.isRight());
    assertEquals(0, result.right().length);
  }

  @Test
  public void many_withTransfor_parsesAllMatches() {
    Parser<Character, Object[]> manyAParser =
        many(
            (Character c) -> c == 'a',
            (List<Character> list) ->
                list.stream().map((Character c) -> 'A').toArray());
    Either<String, Object[]> result = manyAParser.parse(manyAText);
    assertTrue(result.isRight());
    assertEquals(3, result.right().length);
  }

  @Test
  public void many1_isLeftOnEmptyList() {
    Parser<Character, List<Character>> manyAParser = many1((c) -> c == 'b');
    Either<String, List<Character>> result = manyAParser.parse(digitText);
    assertTrue(result.isLeft());
  }

  @Test
  public void many1_parsesAllMatches() {
    Parser<Character, List<Character>> manyAParser = many1((c) -> c == 'a');
    Either<String, List<Character>> result = manyAParser.parse(manyAText);
    assertTrue(result.isRight());
    assertEquals(3, result.right().size());
  }

  @Test
  public void many1_withTransform_isLeftOnEmptyResult() {
    Parser<Character, Object[]> manyAParser =
        many1(
            (Character c) -> false,
            (List<Character> list) ->
                list.stream().map((Character c) -> 'A').toArray());
    Either<String, Object[]> result = manyAParser.parse(digitText);
    assertTrue(result.isLeft());
  }

  @Test
  public void many1_withTransfor_parsesAllMatches() {
    Parser<Character, Object[]> manyAParser =
        many1(
            (Character c) -> c == 'a',
            (List<Character> list) ->
                list.stream().map((Character c) -> 'A').toArray());
    Either<String, Object[]> result = manyAParser.parse(manyAText);
    assertTrue(result.isRight());
    assertEquals(3, result.right().length);
  }

  @Test
  public void maybe_returnsRightWithOptionalValue_onSuccess() {
    Parser<Character, Optional<Character>> alphabeticParser =
        maybe(one((c) -> true, (c) -> c));
    Either<String, Optional<Character>> result =
        alphabeticParser.parse(alphabeticText);
    assertTrue(result.isRight());
    assertEquals(new Character('a'), result.right().get());
  }

  @Test
  public void maybe_returnsRightWithEmptyOptional_onFail() {
    Parser<Character, Optional<Character>> alphabeticParser =
        maybe(one((c) -> false, (c) -> c));
    Either<String, Optional<Character>> result =
        alphabeticParser.parse(alphabeticText);
    assertTrue(result.isRight());
    assertFalse(result.right().isPresent());

  }

  @Test
  public void one_returnsRightOnSuccess() {
    Parser<Character, Character> alphabeticParser = one();
    Either<String, Character> result = alphabeticParser.parse(alphabeticText);
    assertTrue(result.isRight());
    assertEquals(new Character('a'), result.right());

    // Ensure the iterator has been incremented.
    assertTrue(alphabeticParser.parse(alphabeticText).isLeft());
  }

  @Test
  public void one_returnsLeftOnFail() {
    Parser<Character, Character> alphabeticParser = one();
    assertTrue(alphabeticParser.parse(emptyText).isLeft());
  }

  @Test
  public void one_withTransform_returnsRightOnSuccess() {
    Parser<Character, Character> alphabeticParser = one(
        (c) -> Character.toUpperCase(c));
    Either<String, Character> result = alphabeticParser.parse(alphabeticText);
    assertTrue(result.isRight());
    assertEquals(new Character('A'), result.right());

    // Ensure the iterator has been incremented.
    assertTrue(alphabeticParser.parse(alphabeticText).isLeft());
  }

  @Test
  public void one_withTransform_returnsLeftOnFail() {
    Parser<Character, Character> alphabeticParser = one(
        (c) -> Character.toUpperCase(c));
    assertTrue(alphabeticParser.parse(emptyText).isLeft());
  }

  @Test
  public void one_withPredicate_returnsRightOnSuccess() {
    Parser<Character, Character> alphabeticParser = one(
        (c) -> Character.isAlphabetic(c),
        (c) -> Character.toUpperCase(c));
    Either<String, Character> result = alphabeticParser.parse(alphabeticText);
    assertTrue(result.isRight());
    assertEquals(new Character('A'), result.right());

    // Ensure the iterator has been incremented.
    assertTrue(alphabeticParser.parse(alphabeticText).isLeft());
  }

  @Test
  public void one_withPredicate_returnsLeftOnFail() {
    Parser<Character, Character> alphabeticParser = one(
        (c) -> Character.isAlphabetic(c),
        (c) -> Character.toUpperCase(c));
    assertTrue(alphabeticParser.parse(digitText).isLeft());
  }

  @Test
  public void one_withPredicate_failsOnEndOfIterator() {
    Parser<Character, Character> alphabeticParser = one(
        (c) -> Character.isAlphabetic(c),
        (c) -> Character.toUpperCase(c));
    assertTrue(alphabeticParser.parse(Arrays.asList()).isLeft());
  }

  @Test
  public void or_emptyArrayReturnsLeft() {
    Parser<Character, Character> emptyParser = or();
    assertTrue(emptyParser.parse(alphabeticText).isLeft());
  }

  @Test
  public void or_returnsLeftOnFirstMatch() {
    Parser<Character, Character> parsers = or (
        one((c) -> c == '1', (c) -> '0'),
        one((c) -> c == 'a', (c) -> 'A'),
        one((c) -> c == 'b', (c) -> 'B')
      );
    Either<String, Character> result = parsers.parse(alphabeticText);
    assertTrue(result.isRight());
    assertEquals(new Character('A'), result.right());
  }
}