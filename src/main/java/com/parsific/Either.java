package com.parsific;

import java.util.Optional;

public final class Either<L, R> {

  public static <L, R> Either<L, R> left(L left) {
    return new Either<>(Optional.of(left), Optional.empty());
  }

  public static <L, R> Either<L, R> right(R right) {
    return new Either<>(Optional.empty(), Optional.of(right));
  }
  
  public final Optional<L> left;
  public final Optional<R> right;

  private Either(Optional<L> optLeft, Optional<R> optRight) {
    this.left = optLeft;
    this.right = optRight;
  }

  public boolean isLeft() {
    return left.isPresent();
  }

  public boolean isRight() {
    return right.isPresent();
  }
}