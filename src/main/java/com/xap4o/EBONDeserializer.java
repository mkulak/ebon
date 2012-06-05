package com.xap4o;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EBONDeserializer {
    private ByteBuffer buf;

    public Object deserialize(byte[] bytes) {
        buf = ByteBuffer.wrap(bytes);
        buf.put(bytes);
        buf.rewind();
        return readValue();
    }

    private Object readValue() {
        byte valType = buf.get();
        switch (valType) {
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
            case EBON.C_BINARY:
                return readByteArray();
            case EBON.C_LIST:
                return readList();
            case EBON.C_MAP:
                return readMap();
            case EBON.C_DOCUMENT:
                return readDocument();
        }
        return new EBONException("Unsupported type: " + valType);
    }

    private Object readDocument() {
        String className = readString();
        int fieldsCount = buf.getInt();
        Object res = Reflector.newInstance(className);
        Map<String, Field> name2field = Reflector.getFields(res.getClass());
        for (int i = 0; i < fieldsCount; i++) {
            String name = readString();
            Object value = readValue();
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

    private Map<String, Object> readMap() {
        int size = buf.getInt();
        Map<String, Object> res = new HashMap<String, Object>();
        for (int i = 0; i < size; i++) {
            String key = readString();
            res.put(key, readValue());
        }
        return res;
    }

    private List readList() {
        int size = buf.getInt();
        List res = new ArrayList();
        for (int i = 0; i < size; i++) {
            res.add(readValue());
        }
        return res;
    }
}
