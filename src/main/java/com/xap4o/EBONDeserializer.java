package com.xap4o;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Map;

public class EBONDeserializer {
    private ByteBuffer buf;

    public Object deserialize(byte[] bytes) {
        buf = ByteBuffer.wrap(bytes);
        buf.put(bytes);
        buf.rewind();
        return readDocument();
    }

    private Object readValue(byte valType) {
        switch(valType) {
            case EBON.C_NULL:
                return null;
            case EBON.C_BOOLEAN:
                return buf.get() == 1;
            case EBON.C_INT:
                return buf.getInt();
            case EBON.C_LONG:
                return buf.getLong();
            case EBON.C_DOUBLE:
                return buf.getDouble();
            case EBON.C_STRING:
                return readString();
            case EBON.C_BYTES:
                return readByteArray();
            case EBON.C_DOCUMENT:
                return readDocument();
        }
        return new EBONException("Unsupported type: " + valType);
    }

    private Object readDocument() {
        String className = readString();
        Object res = Reflector.newInstance(className);
        Map<String,Field> name2field = Reflector.getFields(res.getClass());
        while (buf.hasRemaining()) {
            String name = readString();
            byte valType = buf.get();
            Object value = readValue(valType);
            try {
                name2field.get(name).set(res, value);
            } catch (Exception e) {
                throw new EBONException("Cannot set field " + name + " for clazz " + className, e);
            }
        }
        return res;
    }

    private String readString() {
        byte[] bytes = readByteArray();
        try {
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            throw new EBONException("", e);
        }
    }

    private byte[] readByteArray() {
        int size = buf.getInt();
        byte[] bytes = new byte[size];
        buf.get(bytes);
        return bytes;
    }
}
