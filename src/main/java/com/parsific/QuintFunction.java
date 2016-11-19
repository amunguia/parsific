package com.parsific;

public interface QuintFunction<A, B, C, D, E, T> {
    T apply(A a, B b, C c, D d, E e);
}