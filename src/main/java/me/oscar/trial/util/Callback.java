package me.oscar.trial.util;

public interface Callback<K,T> {
    T apply(K k) throws NoSuchMethodException;
}
