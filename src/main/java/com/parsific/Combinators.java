package com.parsific;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Combinators {

  public static <A, B, S, T> Parser<S, T> and(
      BiFunction<A, B, T> f, Parser<S, A> aParser, Parser<S, B> bParser) {
    return (iterator) -> {
      Either<String, A> aResult = aParser.parse(iterator);
      Either<String, B> bResult = bParser.parse(iterator);
      return checkForFailureAndException(
        () -> f.apply(aResult.right(), bResult.right()),
        aResult, bResult);
    };
  }

  public static <A, B, C, S, T> Parser<S, T> and(
      TriFunction<A, B, C, T> f,
      Parser<S, A> aParser,
      Parser<S, B> bParser,
      Parser<S, C> cParser) {
    return (iterator) -> {
      Either<String, A> aResult = aParser.parse(iterator);
      Either<String, B> bResult = bParser.parse(iterator);
      Either<String, C> cResult = cParser.parse(iterator);
      return checkForFailureAndException(
        () -> f.apply(aResult.right(), bResult.right(), cResult.right()),
        aResult, bResult, cResult);
    };
  }

  public static <A, B, C, D, S, T> Parser<S, T> and(
      QuadFunction<A, B, C, D, T> f,
      Parser<S, A> aParser,
      Parser<S, B> bParser,
      Parser<S, C> cParser,
      Parser<S, D> dParser) {
    return (iterator) -> {
      Either<String, A> aResult = aParser.parse(iterator);
      Either<String, B> bResult = bParser.parse(iterator);
      Either<String, C> cResult = cParser.parse(iterator);
      Either<String, D> dResult = dParser.parse(iterator);
      return checkForFailureAndException(
        () -> f.apply(
            aResult.right(),
            bResult.right(),
            cResult.right(),
            dResult.right()),
        aResult, bResult, cResult, dResult);
    };
  }

  public static <A, B, C, D, E, S, T> Parser<S, T> and(
      QuintFunction<A, B, C, D, E, T> f,
      Parser<S, A> aParser,
      Parser<S, B> bParser,
      Parser<S, C> cParser,
      Parser<S, D> dParser,
      Parser<S, E> eParser) {
    return (iterator) -> {
      Either<String, A> aResult = aParser.parse(iterator);
      Either<String, B> bResult = bParser.parse(iterator);
      Either<String, C> cResult = cParser.parse(iterator);
      Either<String, D> dResult = dParser.parse(iterator);
      Either<String, E> eResult = eParser.parse(iterator);
      return checkForFailureAndException(
        () -> f.apply(
            aResult.right(),
            bResult.right(),
            cResult.right(),
            dResult.right(),
            eResult.right()),
        aResult, bResult, cResult, dResult, eResult);
    };
  }

  public static <S, T> Parser<S, T> dropLeft(
      Parser<S, T> main, Parser<S, ?> ... drop) {
    return (iterator) -> {
      for (int i = 0; i < drop.length; i++) {
        Either<String, ?> dropResult = drop[i].parse(iterator);
        if (dropResult.isLeft()) {
          return Either.left(dropResult.left());
        }
      }

      return main.parse(iterator);
    };
  }

  public static <S, T> Parser<S, T> dropRight(
      Parser<S, T> main, Parser<S, ?> ... drop) {
    return (iterator) -> {
      Either<String, T> result = main.parse(iterator);
      if (result.isLeft()) {
        return result;
      }
      for (int i = 0; i < drop.length; i++) {
        Either<String, ?> dropResult = drop[i].parse(iterator);
        if (dropResult.isLeft()) {
          return Either.left(dropResult.left());
        }
      }
      return result;
    };
  }

  public static <S> Parser<S, EOF> end() {
    return (iterator) -> {
      return iterator.hasNext()
          ? Either.left("Input still existed, but expected end of input.")
          : Either.right(EOF.instance);
    };
  }

  /**
   * Returns a parser that always succeeds. If the provided parser is
   * successful, this parser will return an Optional with a value. If the
   * provided parser fails, this parser will return an empty Optional. This
   * parser ensures that the iterator pointer is not updated on failure.
   */
  public static <S, T> Parser<S, Optional<T>> maybe(
      final Parser<S, T> parser) {
    return (iterator) -> {
      iterator.wind();
      Either<String, T> result = parser.parse(iterator);
      if (result.isRight()) {
        iterator.clearWind();
        return Either.right(Optional.of(result.right()));
      } else {
        iterator.unwind();
        return Either.right(Optional.empty());
      }
    };
  }

  /**
   * Apply a function the the result of a parser if the parse was successful.
   */
  public static <S, T, U> Parser<S, U> map(
      Parser<S, T> parser, Function<T, U> f) {
    return (iterator) -> {
      Either<String, T> result = parser.parse(iterator);
      return checkForFailureAndException(
          () -> f.apply(result.right()), result);
    };
  }

  /**
   * Attempt the provided parsers in order until one succeeds. This parser fails
   * if none of the parsers pass. The iterator is reset after each fail.
   */
  public static <S, T> Parser<S, T> or(Parser<S, T> ... parsers) {
    return (iterator) -> {
      for (int i = 0; i < parsers.length; i++) {
        Either<String, T> result = parsers[i].parse(iterator.wind());
        if (result.isRight()) {
          iterator.clearWind();
          return result;
        }
        iterator.unwind();
      }
      return Either.left("No parser succeeded.");
    };
  }

  public static <S, T> Parser<S, T> orDefault(
      Parser<S, T> parser, T defaultValue) {
    return (iterator) -> {
      Either<String, T> result = parser.parse(iterator);
      return result.isRight() ? result : Either.right(defaultValue);
    };
  }

  interface Callable<T> {
    T call();
  }

  interface TriFunction<A, B, C, T> {
    T apply(A a, B b, C c);
  }

  interface QuadFunction<A, B, C, D, T> {
    T apply(A a, B b, C c, D d);
  }

  interface QuintFunction<A, B, C, D, E, T> {
    T apply(A a, B b, C c, D d, E e);
  }

  private static Optional<String> checkForFailure(
      Either<String, ?> ... eithers) {
    for (int i = 0; i < eithers.length; i++) {
      if (eithers[i].isLeft()) {
        return Optional.of(eithers[i].left());
      }
    }
    return Optional.empty();
  }

  private static <S> Either<String, S> checkForException(Callable<S> callable) {
    try {
      return Either.right(callable.call());
    } catch (Exception e) {
      return Either.left(e.getMessage());
    }
  }

  private static <T> Either<String, T> checkForFailureAndException(
      Callable<T> callable, Either<String, ?> ... eithers) {
    Optional<String> optFailure = checkForFailure(eithers);
    if (optFailure.isPresent()) {
      return Either.left(optFailure.get());
    }
    return checkForException(callable);
  }

  static class EOF {

    public static EOF instance = new EOF();

    private EOF() {}

  }
}