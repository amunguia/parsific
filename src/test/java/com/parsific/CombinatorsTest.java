package com.parsific;

import static com.parsific.Combinators.*;
import static com.parsific.Parsers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Optional;
import java.util.function.Function;

public class CombinatorsTest {

  @Test
  public void and2_appliesFunctionOnSuccess() {
    Parser<Character, Character> andP =
        and((a, b) -> 'A', one('a'), one('b'));
    assertEquals(new Character('A'), andP.parse(toIterator("ab")).right());
  }

  @Test
  public void and3_appliesFunctionOnSuccess() {
    Parser<Character, Character> andP =
        and((a, b, c) -> 'A', one('a'), one('b'), one('c'));
    assertEquals(new Character('A'), andP.parse(toIterator("abc")).right());
  }

  @Test
  public void and4_appliesFunctionOnSuccess() {
    Parser<Character, Character> andP =
        and((a, b, c, d) -> 'A', one('a'), one('b'), one('c'), one('d'));
    assertEquals(new Character('A'), andP.parse(toIterator("abcd")).right());
  }

  @Test
  public void and5_appliesFunctionOnSuccess() {
    Parser<Character, Character> andP =
        and(
          (a, b, c, d, e) -> 'A',
          one('a'), one('b'), one('c'), one('d'), one('e'));
    assertEquals(new Character('A'), andP.parse(toIterator("abcde")).right());
  }


  @Test
  public void maybe_returnsNonEmptyOptionalOnSuccess() {
    Optional<Character> optChar =
        maybe(one('a')).parse(toIterator("a")).right();
    assertTrue(optChar.isPresent());
    assertEquals(new Character('a'), optChar.get());
  }

  @Test
  public void maybe_returnsEmptyOptionalOnFail() {
    Optional<Character> optChar =
        maybe(one('a')).parse(toIterator("b")).right();
    assertFalse(optChar.isPresent());
  }

  @Test
  public void maybe_doesNotMoveIteratorOnFail() {
    UnwindingIterator<Character> text = toIterator("b");
    Parser<Character, Optional<Character>> failP = (iterator) -> {
      iterator.next(); // Purposefully advance the iterator pointer.
      return Either.left("Failed");
    };
    maybe(failP).parse(text).right();
    Optional<Character> optB =
        maybe(one('b')).parse(text).right();
    assertEquals(new Character('b'), optB.get());
  }

  @Test
  public void map_appliesTheFunctionOnSuccess() {
    Parser<Character, Character> mappedP = map(one(), c -> 'A');
    assertEquals(new Character('A'), mappedP.parse(toIterator("a")).right());
  }

  @Test
  public void map_catchesExceptionFromFunction() {
    String errorMessage = "Bad";
    Parser<Character, Character> mappedP = map(one(), c -> {
      throw new RuntimeException(errorMessage);
    });
    assertEquals(errorMessage, mappedP.parse(toIterator("a")).left());
  }

  @Test
  public void map_preservesTheErrorMessageOnFail() {
    String failMessage = "Fail";
    Function<Character, Character> f = c -> {
      throw new RuntimeException(failMessage);
    };
    Parser<Character, Character> mappedP = map(one(), f);
    assertEquals(failMessage, mappedP.parse(toIterator("a")).left());
  }

  @Test
  public void or_returnsFirstPassingParser() {
    Parser<Character, Character> orP = or(one('a'), one('b'), one('c'));
    assertEquals(new Character('b'), orP.parse(toIterator("b")).right());
  }

  @Test
  public void or_failsIfNoParserPasses() {
    Parser<Character, Character> orP = or(one('a'), one('b'), one('c'));
    assertFalse(orP.parse(toIterator("d")).isRight());
  }

  @Test
  public void or_doesNotUpdateIteratorOnFail() {
    UnwindingIterator<Character> iterator = toIterator("d");
    or(one('a'), one('b'), one('c')).parse(iterator);
    assertEquals(new Character('d'), iterator.peek());
  }

  @Test
  public void or_onlyUpdatesIteratorForOneParser() {
    UnwindingIterator<Character> iterator = toIterator("cd");
    or(one('a'), one('b'), one('c')).parse(iterator);
    assertEquals(new Character('d'), iterator.peek());
  }

  @Test
  public void sepBy() {

  }

  private UnwindingIterator<Character> toIterator(String string) {
    return new UnwindingIterator<>(new ArrayLikeString(string));
  }
}