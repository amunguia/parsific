package com.parsific;

public interface QuadFunction<A, B, C, D, T> {
    T apply(A a, B b, C c, D d);
}