package com.parsific;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.parsific.Either.EitherException;
import org.junit.Test;

public class EitherTest {

  @Test(expected = EitherException.class)
  public void left_ThrowsExceptionOnNull() throws EitherException {
    Either.left(null);
  }

  @Test
  public void left_setsLeft() throws EitherException {
    Either<Integer, String> left = Either.left(1);
    assertEquals("Left == 1", 1, left.left.get().intValue());
    assertFalse("Right is not present", left.right.isPresent());
  }

  @Test
  public void right_setsRight() throws EitherException {
    Either<Integer, String> right = Either.right("Hello");
    assertEquals("Right == 'hello'", "Hello", right.right.get());
    assertFalse("Left is not present", right.left.isPresent());
  }

  @Test(expected = EitherException.class)
  public void right_ThrowsExceptionOnNull() throws EitherException {
    Either.right(null);
  }

}