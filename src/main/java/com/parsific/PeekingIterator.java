package com.parsific;

import java.util.Iterator;

public interface PeekingIterator<E> extends Iterator<E> {

  E peek();

}
