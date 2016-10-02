package com.parsific;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Parsers {

  public static <S, A, B, T> Parser<S, T> and(
      Parser<S, A> aParser,
      Parser<S, B> bParser,
      BiFunction<A, B, T> transform) {
    return and_(aParser, bParser, null, null, null, transform, null,
        null, null);
  }

  public static <S, A, B, C, T> Parser<S, T> and(
      Parser<S, A> aParser,
      Parser<S, B> bParser,
      Parser<S, C> cParser,
      TriFunction<A, B, C, T> transform) {
    return and_(aParser, bParser, cParser, null, null, null, transform,
        null, null);
  }

  public static <S, A, B, C, D, T> Parser<S, T> and(
      Parser<S, A> aParser,
      Parser<S, B> bParser,
      Parser<S, C> cParser,
      Parser<S, D> dParser,
      QuadFunction<A, B, C, D, T> transform) {
    return and_(aParser, bParser, cParser, dParser, null, null, null,
        transform, null);
  }

  public static <S, A, B, C, D, E, T> Parser<S, T> and(
      Parser<S, A> aParser,
      Parser<S, B> bParser,
      Parser<S, C> cParser,
      Parser<S, D> dParser,
      Parser<S, E> eParser,
      QuintFunction<A, B, C, D, E, T> transform) {
    return and_(aParser, bParser, cParser, dParser, eParser, null, null,
        null, transform);
  }

  public static <S> Parser<S, List<S>> many(Predicate<S> predicate) {
    return (iterator) -> {
      return Either.right(accumulate(iterator, predicate));
    };
  }

  public static <S, T> Parser<S, T> many(
      Predicate<S> predicate, Function<List<S>, T> transform) {
    return (iterator) -> {
      return Either.right(transform.apply(accumulate(iterator, predicate)));
    };
  }

  public static <S> Parser<S, List<S>> many1(Predicate<S> predicate) {
    return (iterator) -> {
      List<S> list = accumulate(iterator, predicate);
      if (list.isEmpty()) {
        return Either.left("Expected at least one element.");
      }
      return Either.right(list);
    };
  }

  public static <S, T> Parser<S, T> many1(
      Predicate<S> predicate, Function<List<S>, T> transform) {
    return (iterator) -> {
      List<S> list = accumulate(iterator, predicate);
      if (list.isEmpty()) {
        return Either.left("Expected at least one element.");
      }
      return Either.right(transform.apply(list));
    };
  }

  public static <S, T> Parser<S, Optional<T>> maybe(
      final Parser<S, T> parser) {
    return (iterator) -> {
      Either<String, T> result = parser.parse(iterator);
      return result.isRight() 
          ? Either.right(Optional.of(result.right()))
          : Either.right(Optional.empty());
    };
  }

  public static <S> Parser<S, S> one() {
    return (iterator) -> {
      if (!iterator.hasNext()) {
        return Either.left("Attempting to parse one at end of iterator.");
      }
      return Either.right(iterator.next());
    };
  }

  public static <S, T> Parser<S, T> one(Function<S, T> transform) {
    return (iterator) -> {
      if (!iterator.hasNext()) {
        return Either.left("Attempting to parse one at end of iterator.");
      }
      return Either.right(transform.apply(iterator.next()));
    };
  }

  public static <S, T> Parser<S, T> one(
      Predicate<S> predicate, Function<S, T> transform) {
    return (iterator) -> {
      if (!iterator.hasNext()) {
        return Either.left("Attempting to parse one at end of iterator.");
      }
      if (predicate.test(iterator.peek())) {
        return Either.right(transform.apply(iterator.next()));
      }
      return Either.left(
        "Expected to parse one element, but element did not pass predicate.");
    };
  }

  public static <S, T> Parser<S, T> or(Parser<S, T> ... parsers) {
    return (iterator) -> {
      for (int i = 0; i < parsers.length; i++) {
        Either<String, T> result = parsers[i].parse(iterator.wind());
        if (result.isRight()) {
          return result;
        }
        iterator.unwind();
      }
      return Either.left("No parser succeeded.");
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

  private static <S, A, B, C, D, E, R> Parser<S, R> and_(
     Parser<S, A> p1, Parser<S, B> p2, Parser<S, C> p3, Parser<S, D> p4,
     Parser<S, E> p5, BiFunction<A, B, R> f2, TriFunction<A, B, C, R> f3,
     QuadFunction<A, B, C, D, R> f4, QuintFunction<A, B, C, D, E, R> f5) {
    return (iterator) -> {
      Either<String, A> firstResult = p1.parse(iterator);
      if (firstResult.isLeft()) {
        return Either.left(firstResult.left());
      }

      Either<String, B> secondResult = p2.parse(iterator);
      if (secondResult.isLeft()) {
        return Either.left(secondResult.left());
      }
      if (p3 == null) {
        return Either.right(
          f2.apply(firstResult.right(), secondResult.right()));
      }

      Either<String, C> thirdResult = p3.parse(iterator);
      if (thirdResult.isLeft()) {
        return Either.left(thirdResult.left());
      }
      if (p4 == null) {
        return Either.right(f3.apply(
            firstResult.right(), secondResult.right(), thirdResult.right()));
      }

      Either<String, D> fourthResult = p4.parse(iterator);
      if (fourthResult.isLeft()) {
        return Either.left(fourthResult.left());
      }
      if (p5 == null) {
        return Either.right(f4.apply(firstResult.right(), secondResult.right(),
            thirdResult.right(), fourthResult.right()));
      }

      Either<String, E> fifthResult = p5.parse(iterator);
      if (fifthResult.isLeft()) {
        Either.left(secondResult.left());
      }
      return Either.right(f5.apply(firstResult.right(), secondResult.right(),
          thirdResult.right(), fourthResult.right(), fifthResult.right()));
    };
  }

  public interface TriFunction<A, B, C, R> {
    public R apply(A a, B b, C c);
  }

  public interface QuadFunction<A, B, C, D, R> {
    public R apply(A a, B b, C c, D d);
  }

  public interface QuintFunction<A, B, C, D, E, R> {
    public R apply(A a, B b, C c, D d, E e);
  }
}