package com.parsific;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

public interface Parser<S, T> {

	 Either<String, T> parse(UnwindingIterator<S> iterator);

  default Either<String, T> parse(S[] array) {
    return parse(
      new UnwindingIterator<S>(
        new ArrayLikeArray<S>(array)));
  }

  default Either<String, T> parse(List<S> list) {
    return parse(
      new UnwindingIterator<S>(
        new ArrayLikeList<S>(list)));
  }

  default Either<String, T> parse(ArrayLike<S> arrayLike) {
    return parse(
      new UnwindingIterator<>(arrayLike));
  }

}
