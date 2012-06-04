package com.xap4o;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class BasicTest {
    @Test
    public void simple() {
        Foo f = new Foo();
        f.a = 2;
        f.bar = Long.MAX_VALUE;
        f.c = true;
        f.stringField = "some string";
        byte[] bytes = EBON.serialize(f);
        Foo newF = EBON.deserialize(bytes);
        assertEquals(f.a, newF.a);
        assertEquals(f.bar, newF.bar);
        assertEquals(f.c, newF.c);
        assertEquals(f.stringField, newF.stringField);
        assertEquals(f.nullField, newF.nullField);
    }

    public static class Foo {
        public int a;
        public long bar;
        public boolean c;
        public String stringField;
        public Object nullField;
    }
}
