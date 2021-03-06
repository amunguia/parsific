package com.parsific;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Combinators {

  public static <A, B, S, T> Parser<S, T> and(
      BiFunction<A, B, T> f, Parser<S, A> aParser, Parser<S, B> bParser) {
    return (iterator) -> {
      Either<ParserException, A> aResult = aParser.parse(iterator);
      Either<ParserException, B> bResult = bParser.parse(iterator);
      return checkForFailureAndException(
        iterator.nextIndex(),
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
      Either<ParserException, A> aResult = aParser.parse(iterator);
      Either<ParserException, B> bResult = bParser.parse(iterator);
      Either<ParserException, C> cResult = cParser.parse(iterator);
      return checkForFailureAndException(
        iterator.nextIndex(),
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
      Either<ParserException, A> aResult = aParser.parse(iterator);
      Either<ParserException, B> bResult = bParser.parse(iterator);
      Either<ParserException, C> cResult = cParser.parse(iterator);
      Either<ParserException, D> dResult = dParser.parse(iterator);
      return checkForFailureAndException(
        iterator.nextIndex(),
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
      Either<ParserException, A> aResult = aParser.parse(iterator);
      Either<ParserException, B> bResult = bParser.parse(iterator);
      Either<ParserException, C> cResult = cParser.parse(iterator);
      Either<ParserException, D> dResult = dParser.parse(iterator);
      Either<ParserException, E> eResult = eParser.parse(iterator);
      return checkForFailureAndException(
        iterator.nextIndex(),
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
        Either<ParserException, ?> dropResult = drop[i].parse(iterator);
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
      Either<ParserException, T> result = main.parse(iterator);
      if (result.isLeft()) {
        return result;
      }
      for (int i = 0; i < drop.length; i++) {
        Either<ParserException, ?> dropResult = drop[i].parse(iterator);
        if (dropResult.isLeft()) {
          return Either.left(dropResult.left());
        }
      }
      return result;
    };
  }

  public static <S, T> Parser<S, T> dropBoth(
        Parser<S, T> middle, Parser<S, ?> left, Parser<S, ?> right) {
    return dropRight(dropLeft(middle, left), right);
  }

  public static <S, T> Parser<S, T> dropBoth(
        Parser<S, T> middle, Parser<S, ?>[] left, Parser<S, ?>[] right) {
    return dropRight(dropLeft(middle, left), right);
  }

  public static <S> Parser<S, EOF> end() {
    return (iterator) -> {
      return iterator.hasNext()
          ? Either.left(new ParserException(iterator.nextIndex()))
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
      Either<ParserException, T> result = parser.parse(iterator);
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
      Either<ParserException, T> result = parser.parse(iterator);
      return checkForFailureAndException(
          iterator.nextIndex(), () -> f.apply(result.right()), result);
    };
  }

  /**
   * Attempt the provided parsers in order until one succeeds. This parser fails
   * if none of the parsers pass. The iterator is reset after each fail.
   */
  public static <S, T> Parser<S, T> or(Parser<S, T> ... parsers) {
    return (iterator) -> {
      for (int i = 0; i < parsers.length; i++) {
        Either<ParserException, T> result = parsers[i].parse(iterator.wind());
        if (result.isRight()) {
          iterator.clearWind();
          return result;
        }
        iterator.unwind();
      }
      return Either.left(new ParserException(iterator.nextIndex()));
    };
  }

  public static <S, T> Parser<S, T> orDefault(
      Parser<S, T> parser, T defaultValue) {
    return (iterator) -> {
      Either<ParserException, T> result = parser.parse(iterator);
      return result.isRight() ? result : Either.right(defaultValue);
    };
  }

  public static <S, T> Parser<S, LinkedList<T>> seperatedBy(
      Parser<S, T> parser, S delimiter) {
    Parser<S, T> seperatedParser = and(
      (delim, entry) -> entry, Parsers.one(delimiter), parser);
    BiFunction<T, LinkedList<T>, LinkedList<T>> prependF = (first, list) -> {
      list.addFirst(first);
      return list;
    };
    return and(prependF, parser, Parsers.manyParser(seperatedParser));
  }

  interface Callable<T> {
    T call();
  }

  private static Optional<ParserException> checkForFailure(
      Either<ParserException, ?> ... eithers) {
    for (int i = 0; i < eithers.length; i++) {
      if (eithers[i].isLeft()) {
        return Optional.of(eithers[i].left());
      }
    }
    return Optional.empty();
  }

  private static <S> Either<ParserException, S> checkForException(
      int iteratorIndex, Callable<S> callable) {
    try {
      return Either.right(callable.call());
    } catch (Exception e) {
      return Either.left(new ParserException(iteratorIndex));
    }
  }

  private static <T> Either<ParserException, T> checkForFailureAndException(
      int iteratorIndex, Callable<T> callable, Either<ParserException, ?> ... eithers) {
    Optional<ParserException> optFailure = checkForFailure(eithers);
    if (optFailure.isPresent()) {
      return Either.left(optFailure.get());
    }
    return checkForException(iteratorIndex, callable);
  }

  static class EOF {

    public static EOF instance = new EOF();

    private EOF() {}

  }
}