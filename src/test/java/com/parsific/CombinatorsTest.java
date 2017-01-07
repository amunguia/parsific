package com.parsific;

import static com.parsific.Combinators.*;
import static com.parsific.Parsers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.LinkedList;
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
  public void dropLeft_onlyReturnsDesiredValue() {
    Parser<Character, Character> dropLeftP =
      dropLeft(one('c'), one('a'), one('b'));
    assertEquals(
        new Character('c'), dropLeftP.parse(toIterator("abc")).right());
  }

  @Test
  public void dropLeft_failsIfAnyDroppedParserFails() {
    Parser<Character, Character> dropLeftP =
      dropLeft(one('c'), one('a'), one('b'));
    assertFalse(dropLeftP.parse(toIterator("aac")).isRight());
  }

  @Test
  public void dropLeft_failsIfTheMainParserFails() {
    Parser<Character, Character> dropLeftP =
      dropLeft(one('c'), one('a'), one('b'));
    assertFalse(dropLeftP.parse(toIterator("aba")).isRight());
  }

  @Test
  public void dropRight_onlyReturnsDesiredValue() {
    Parser<Character, Character> dropRightR =
      dropRight(one('a'), one('b'), one('c'));
    assertEquals(
        new Character('a'), dropRightR.parse(toIterator("abc")).right());
  }

  @Test
  public void dropRight_failsIfAnyDroppedParserFails() {
    Parser<Character, Character> dropRightR =
      dropRight(one('a'), one('b'), one('c'));
    assertFalse(dropRightR.parse(toIterator("abb")).isRight());
  }

  @Test
  public void dropRight_failsIfTheMainParserFails() {
    Parser<Character, Character> dropRightR =
      dropRight(one('a'), one('b'), one('c'));
    assertFalse(dropRightR.parse(toIterator("bbc")).isRight());
  }

  @Test
  public void end_succeedsAtEndOfInput() {
    Parser<Character, EOF> endP = end();
    assertTrue(endP.parse(toIterator("")).isRight());
  }

  @Test
  public void end_failsWhenNotAtEndOfInput() {
    Parser<Character, EOF> endP = end();
    assertFalse(endP.parse(toIterator("a")).isRight());
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
      return Either.left(new ParserException(1));
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
    assertEquals(1, mappedP.parse(toIterator("a")).left().getErrorIndex());
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
  public void orDefault_returnsSuccessParsedValue() {
    Parser<Character, Character> orDefaultP = orDefault(one('a'), '!');
    assertEquals(
      new Character('a'), orDefaultP.parse(toIterator("a")).right());
  }

  @Test
  public void orDefault_returnsDefaultValueOnParseFail() {
    Parser<Character, Character> orDefaultP = orDefault(one('a'), '!');
    assertEquals(
      new Character('!'), orDefaultP.parse(toIterator("b")).right());
  }

  @Test
  public void seperatedBy_failsWhenCantMatchOne() {
    Parser<Character, LinkedList<Character>> sepP = seperatedBy(one('a'), ',');
    assertFalse(sepP.parse(toIterator("b")).isRight());
  }

  @Test
  public void seperatedBy_matchesOne() {
    Parser<Character, LinkedList<Character>> sepP = seperatedBy(one('a'), ',');
    Either<ParserException, LinkedList<Character>> result = sepP.parse(toIterator("ab"));
    assertEquals(1, result.right().size());
  }

  @Test
  public void seperatedBy_matchesAll() {
    Parser<Character, LinkedList<Character>> sepP = seperatedBy(one('a'), ',');
    UnwindingIterator<Character> iterator = toIterator("a,a,a,a,b");
    Either<ParserException, LinkedList<Character>> result = sepP.parse(iterator);
    assertEquals(4, result.right().size());
    for (Character c : result.right()) {
      assertEquals(new Character('a'), c);
    }
    assertEquals(new Character(','), iterator.peek());
  }

  @Test
  public void testParserException_index() {
    Parser<Character, Character> oneBParser = and(
      (a, b, c, d, e) -> b,
      one('a'), one('b'), one('c'), one('d'), one('e'));
    Either<ParserException, Character> result = oneBParser.parse(toIterator("abc!e"));
    assertTrue(result.isLeft());
    assertEquals(3, result.left().getErrorIndex());
  }

  private UnwindingIterator<Character> toIterator(String string) {
    return new UnwindingIterator<>(new ArrayLikeString(string));
  }
}