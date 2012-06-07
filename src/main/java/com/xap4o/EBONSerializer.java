package com.xap4o;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public class EBONSerializer {
    private ByteBuffer buf = ByteBuffer.allocate(1024 * 100);

    public byte[] serialize(Object doc) {
        writeValue(doc);
        buf.flip();
        byte[] result = new byte[buf.remaining()];
        buf.get(result, 0, result.length);
        return result;
    }

    private void writeDocument(Object doc) {
        writeString(doc.getClass().getName());
        Map<String,Field> fieldsMap = Reflector.getFields(doc.getClass());
        int pos = buf.position();
        buf.putInt(0);//to reserve space for actual fieldsCount value
        int fieldsCount = 0;
        for (Map.Entry<String, Field> e : fieldsMap.entrySet()) {
            if (e.getValue().getAnnotation(Skip.class) == null) {
                writeString(e.getKey());
                try {
                    writeValue(e.getValue().get(doc));
                } catch (Exception ex) {
                    throw new EBONException("", ex);
                }
                fieldsCount++;
            }
        }
        buf.putInt(pos, fieldsCount);
    }

    private void writeString(String str) {
        try {
            byte[] bytes = str.getBytes("UTF-8");
            buf.putInt(bytes.length);
            buf.put(bytes);
        } catch (Exception e) {
            throw new EBONException("", e);
        }
    }

    private void writeValue(Object value) {
        if (value == null) {
            buf.put(EBON.C_NULL);
            return;
        }
        Class<?> clazz = value.getClass();
        if (clazz == Boolean.class) {
            buf.put(EBON.C_BOOLEAN);
            buf.put((Boolean) value ? (byte) 1 : (byte) 0);
        } else if (clazz == Integer.class) {
            buf.put(EBON.C_INT);
            buf.putInt((Integer) value);
        } else if (clazz == Long.class) {
            buf.put(EBON.C_LONG);
            buf.putLong((Long) value);
        } else if (clazz == Double.class) {
            buf.put(EBON.C_DOUBLE);
            buf.putDouble((Double) value);
        } else if (clazz == String.class) {
            buf.put(EBON.C_STRING);
            writeString((String) value);
        } else if (clazz.isArray()) {
            if (clazz.getComponentType() == byte.class) {
                buf.put(EBON.C_BINARY);
                writeByteArray((byte[]) value);
            } else {
                throw new EBONException("Cannot serialize array of " + clazz.getComponentType());
            }
        } else if (List.class.isAssignableFrom(clazz)) {
            buf.put(EBON.C_LIST);
            writeList((List) value);
        } else if (Map.class.isAssignableFrom(clazz)) {
            buf.put(EBON.C_MAP);
            writeMap((Map<String, Object>) value);
        } else if (clazz.isEnum()) {
            buf.put(EBON.C_ENUM);
            writeEnum((Enum) value);
        } else {
            buf.put(EBON.C_DOCUMENT);
            writeDocument(value);
        }
    }

    private void writeEnum(Enum value) {
        writeString(value.getClass().getName());
        writeString(value.name());
    }

    private void writeMap(Map<String, Object> value) {
        buf.putInt(value.size());
        for (Map.Entry<String, Object> e : value.entrySet()) {
            writeString(e.getKey());
            writeValue(e.getValue());
        }
    }

    private void writeList(List value) {
        buf.putInt(value.size());
        for (Object elem : value) {
            writeValue(elem);
        }
    }

    private void writeByteArray(byte[] value) {
        buf.putInt(Array.getLength(value));
        buf.put((byte[]) value);
    }
}
