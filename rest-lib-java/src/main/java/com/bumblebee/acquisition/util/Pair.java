package com.bumblebee.acquisition.util;

import java.io.Serializable;

/**
 * 键值对帮助类
 * Created by renhua.zhang on 2017/3/17.
 */
public class Pair<K, V> implements Serializable {

    /**
     * key
     */
    private K key;

    /**
     * value
     */
    private V value;

    public Pair() {
    }

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
