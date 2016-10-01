package com.parsific;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class UnwindingIteratorTest {

  private UnwindingIterator<Integer> unwindingIterator;

  @Before
  public void setUp() {
    unwindingIterator = new UnwindingIterator(Arrays.asList(1, 2, 3, 4, 5));
  }

  @Test
  public void hasNext_returnsCorrectValue() {
    assertTrue(unwindingIterator.hasNext());
    unwindingIterator.next();
    assertTrue(unwindingIterator.hasNext());
    unwindingIterator.next();
    assertTrue(unwindingIterator.hasNext());
    unwindingIterator.next();
    assertTrue(unwindingIterator.hasNext());
    unwindingIterator.next();
    assertTrue(unwindingIterator.hasNext());
    unwindingIterator.next();
    assertFalse(unwindingIterator.hasNext());
  }

  @Test
  public void next_returnsCorrectValue() {
    assertEquals(1, unwindingIterator.next().intValue());
    assertEquals(2, unwindingIterator.next().intValue());
    assertEquals(3, unwindingIterator.next().intValue());
    assertEquals(4, unwindingIterator.next().intValue());
    assertEquals(5, unwindingIterator.next().intValue());
  }

  @Test
  public void unwind_resetsToPreviousState() {
    unwindingIterator.wind();
    skipToEnd();
    assertFalse(unwindingIterator.hasNext());

    unwindingIterator.unwind();
    assertTrue(unwindingIterator.hasNext());
    assertEquals(1, unwindingIterator.next().intValue());
  }

  @Test
  public void unwind_withMultiWind() {
    unwindingIterator.wind();
    unwindingIterator.next();
    unwindingIterator.next();
    unwindingIterator.wind();
    skipToEnd();
    assertFalse(unwindingIterator.hasNext());

    unwindingIterator.unwind();
    assertTrue(unwindingIterator.hasNext());
    assertEquals(3, unwindingIterator.next().intValue());
    
    unwindingIterator.unwind();
    assertTrue(unwindingIterator.hasNext());
    assertEquals(1, unwindingIterator.next().intValue());
  }

  private void skipToEnd() {
    while (unwindingIterator.hasNext()) {
      unwindingIterator.next();
    }
  }

}