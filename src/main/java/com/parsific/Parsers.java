package com.parsific;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Parsers {

  /**
   * Returns a parser that returns a list of tokens, in order, if the
   * iterator has the provided tokens in the provided order.
   */
  public static <S> Parser<S, List<S>> all(S ... ss) {
    return (iterator) -> {
      List<S> list = new ArrayList<>();
      for (int i = 0; i < ss.length; i++) {
        if (iterator.hasNext() && iterator.peek().equals(ss[i])) {
          list.add(iterator.next());
        } else {
          return Either.left("Did not match all items.");
        }
      }
      return Either.right(list);
    };
  }

  /**
   * Returns a parser that will succeed if the next token is equal to an
   * element in the provided list.
   */
  public static <S> Parser<S, S> any(S ... ss) {
    return one(s -> {
      for (int i  = 0; i< ss.length; i++) {
        if (s.equals(ss[i])) {
          return true;
        }
      }
      return false;
    });
  }

  /**
   * Returns a parser that always succeeds and returns a list. The elements in
   * the list tokens that pass the predicate, accumulated in order until a
   * token fails the predicate.
   */
  public static <S> Parser<S, List<S>> many(Predicate<S> predicate) {
    return (iterator) -> {
      return Either.right(accumulate(iterator, predicate));
    };
  }

  /**
   * Returns a parser that succeeds if at least the next token passes the
   * predicate. Returns a list of tokens that pass the predicate, accumulated
   * in order until a token fails the predicate.
   */
  public static <S> Parser<S, List<S>> many1(Predicate<S> predicate) {
    return (iterator) -> {
      List<S> list = accumulate(iterator, predicate);
      if (list.isEmpty()) {
        return Either.left("Expected at least one element.");
      }
      return Either.right(list);
    };
  }

  /**
   * Returns a parser that succeeds when the next token is not equal to the
   * provided token.
   */
  public static <S> Parser<S, S> not(S s) {
    return one(s2 -> !s2.equals(s));
  }

  /**
   * Returns a parser that always succeeds, as long as not at the end of the
   * iterator.
   */
  public static <S> Parser<S, S> one() {
    return one(s -> true);
  }

  /**
   * Returns a parser that succeeds if the next token is equal to the provided
   * token.
   */
  public static <S> Parser<S, S> one(S s) {
    return one(s2 -> s2.equals(s));
  }

  /**
   * Returns a parser that succeeds if the next token passes the provided
   * predicate.
   */
  public static <S> Parser<S, S> one(Predicate<S> predicate) {
    return (iterator) -> {
      if (!iterator.hasNext()) {
        return Either.left("Attempting to parse one at end of iterator.");
      }
      if (predicate.test(iterator.peek())) {
        return Either.right(iterator.next());
      }
      return Either.left(
        "Expected to parse one element, but element did not pass predicate.");
    };
  }

  private static <S> List<S> accumulate(
      PeekingIterator<S> iterator, Predicate<S> predicate) {
    List<S> list = new ArrayList<>();
    while (iterator.hasNext() && predicate.test(iterator.peek())) {
      list.add(iterator.next());
    }
    return list;
  }
}