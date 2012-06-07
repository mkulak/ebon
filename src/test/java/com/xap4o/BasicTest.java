package com.xap4o;

import org.junit.Test;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;

public class BasicTest {
    @Test
    public void testInt() {
        Integer a = 1;
        Integer b = EBON.deserialize(EBON.serialize(a));
        assertEquals(a, b);
    }

    @Test
    public void testLong() {
        Long a = (long) (Math.random() * Long.MIN_VALUE);
        Long b = EBON.deserialize(EBON.serialize(a));
        assertEquals(a, b);
    }

    @Test
    public void testDouble() {
        Double a = Math.random() * Double.MAX_VALUE;
        Double b = EBON.deserialize(EBON.serialize(a));
        assertEquals(a, b);
    }

    @Test
    public void testString() {
        String a = "quick brown fox jumps over the lazy dog";
        String b = EBON.deserialize(EBON.serialize(a));
        assertEquals(a, b);
    }

    @Test
    public void testList() {
        List a = Arrays.asList(1, "abc", -3.2, 4L);
        List b = EBON.deserialize(EBON.serialize(a));
        assertEquals(a.size(), b.size());
        for (int i = 0; i < a.size(); i++) {
            assertEquals(a.get(i), b.get(i));
        }
    }

    @Test
    public void testMap() {
        Map<String, Object> a = new HashMap<String, Object>();
        a.put("", 1);
        a.put("abc", "foo-bar-baz");
        a.put("some key", Arrays.asList("abc", 90, 0.99999));
        Map<String, Object> b = EBON.deserialize(EBON.serialize(a));
        assertEquals(a.size(), b.size());
        for (String key : a.keySet()) {
            assertEquals(a.get(key), b.get(key));
        }
    }

    @Test
    public void testCustomClass() {
        Foo f = new Foo();
        f.a = 2;
        f.bar = Long.MAX_VALUE;
        f.c = true;
        f.stringField = "some string";
        Foo newF = EBON.deserialize(EBON.serialize(f));
        assertEquals(f.a, newF.a);
        assertEquals(f.bar, newF.bar);
        assertEquals(f.c, newF.c);
        assertEquals(f.stringField, newF.stringField);
        assertEquals(f.objField, newF.objField);
    }

    @Test
    public void testEnum() {
        TestEnum a = TestEnum.CONST_NAME_1;
        TestEnum b = EBON.deserialize(EBON.serialize(a));
        assertSame(a, b);
    }

    @Test
    public void testSkip() {
        TestSkip a = new TestSkip();
        a.string1 = "john";
        a.string2 = "12345";
        a.string3 = "   ";
        TestSkip b = EBON.deserialize(EBON.serialize(a));
        assertEquals(a.string1, b.string1);
        assertNull(b.string2);
        assertEquals(a.string3, b.string3);
    }

    @Test
    public void testInnerObjects() {
        Bar b1 = new Bar();
        b1.d = Integer.MAX_VALUE;
        b1.doubleField = Double.MIN_VALUE;
        b1.ref = new Foo();
        b1.ref.a = -100;
        b1.ref2 = new Bar();
        b1.ref2.d = 1;
        Bar b2 = EBON.deserialize(EBON.serialize(b1));
        assertEquals(b1, b2);
    }

    public static class Foo {
        public int a;
        public long bar;
        public boolean c;
        public String stringField;
        public Object objField;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Foo foo = (Foo) o;

            if (a != foo.a) return false;
            if (bar != foo.bar) return false;
            if (c != foo.c) return false;
            if (objField != null ? !objField.equals(foo.objField) : foo.objField != null) return false;
            if (stringField != null ? !stringField.equals(foo.stringField) : foo.stringField != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = a;
            result = 31 * result + (int) (bar ^ (bar >>> 32));
            result = 31 * result + (c ? 1 : 0);
            result = 31 * result + (stringField != null ? stringField.hashCode() : 0);
            result = 31 * result + (objField != null ? objField.hashCode() : 0);
            return result;
        }
    }

    public static class Bar {
        private int d;
        private double doubleField;
        private Foo ref;
        private Bar ref2;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Bar bar = (Bar) o;

            if (d != bar.d) return false;
            if (Double.compare(bar.doubleField, doubleField) != 0) return false;
            if (ref != null ? !ref.equals(bar.ref) : bar.ref != null) return false;
            if (ref2 != null ? !ref2.equals(bar.ref2) : bar.ref2 != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = d;
            temp = doubleField != +0.0d ? Double.doubleToLongBits(doubleField) : 0L;
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + (ref != null ? ref.hashCode() : 0);
            result = 31 * result + (ref2 != null ? ref2.hashCode() : 0);
            return result;
        }
    }

    public static enum TestEnum {
        CONST_NAME_1,
        CONST_NAME_2
    }

    public static class TestSkip {
        public String string1;
        @Skip
        public String string2;
        public String string3;
    }
}
