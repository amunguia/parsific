package com.parsific;

import java.util.Optional;

public final class Either<L, R> {

  public static <L, R> Either<L, R> left(L left) throws EitherException {
    if (left == null) {
      throw new EitherException("Cannot create either from null.");
    }
    return new Either<>(Optional.of(left), Optional.empty());
  }

  public static <L, R> Either<L, R> right(R right) throws EitherException {
    if (right == null) {
      throw new EitherException("Cannot create either from null.");
    }
    return new Either<>(Optional.empty(), Optional.of(right));
  }
  
  public final Optional<L> left;
  public final Optional<R> right;

  private Either(Optional<L> optLeft, Optional<R> optRight)
      throws EitherException {
    if (optLeft.isPresent() && optRight.isPresent()) {
      throw new EitherException("Cannot have both left and right.");
    }
    if (!optLeft.isPresent() && !optRight.isPresent()) {
      throw new EitherException("Must have one of left or right.");
    }
    this.left = optLeft;
    this.right = optRight;
  }

  public boolean isLeft() {
    return left.isPresent();
  }

  public boolean isRight() {
    return right.isPresent();
  }


  static class EitherException extends Exception {
    public static final long serialVersionUID = 1L;
    public EitherException(String message) {
      super(message);
    }
  }
}