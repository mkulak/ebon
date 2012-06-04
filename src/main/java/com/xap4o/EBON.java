package com.xap4o;

public class EBON {
    public static final byte C_NULL = 0;
    public static final byte C_BOOLEAN = 1;
    public static final byte C_INT = 2;
    public static final byte C_LONG = 3;
    public static final byte C_DOUBLE = 4;
    public static final byte C_STRING = 5;
    public static final byte C_ARRAY = 6;
    public static final byte C_DOCUMENT = 7;
    public static final byte C_BYTES = 8;
    public static final byte C_MAP = 9;


    public static byte[] serialize(Object doc) {
        return new EBONSerializer().serialize(doc);
    }

    public static <T> T deserialize(byte[] bytes) {
        return (T) new EBONDeserializer().deserialize(bytes);
    }
}
