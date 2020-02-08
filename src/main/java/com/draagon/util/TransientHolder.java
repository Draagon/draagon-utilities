package com.draagon.util;

/**
 * Holds object with a transient reference
 * @author Doug
 */
public class TransientHolder {
    private transient Object o;
    public TransientHolder( Object o ) { this.o = o; }
    public Object get() { return o; }
}
