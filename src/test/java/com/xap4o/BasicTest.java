package com.xap4o;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class BasicTest {
    @Test
    public void testSimple() {
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
        assertEquals(f.objField, newF.objField);
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
        byte[] bytes = EBON.serialize(b1);
        Bar b2 = EBON.deserialize(bytes);
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
}
