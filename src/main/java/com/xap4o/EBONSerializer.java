package com.xap4o;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Map;

public class EBONSerializer {
    private ByteBuffer buf = ByteBuffer.allocate(1024 * 100);

    public byte[] serialize(Object doc) {
        writeDocument(doc);
        buf.flip();
        byte[] result = new byte[buf.remaining()];
        buf.get(result, 0, result.length);
        return result;
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

    private void writeDocument(Object doc) {
        writeString(doc.getClass().getName());
        Map<String,Field> fieldsMap = Reflector.getFields(doc.getClass());
        for (Map.Entry<String, Field> e : fieldsMap.entrySet()) {
            writeString(e.getKey());
            try {
                writeValue(e.getValue().get(doc));
            } catch (Exception ex) {
                throw new EBONException("", ex);
            }
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
            buf.put(((Boolean) value).booleanValue() ? (byte) 1 : (byte) 0);
        } else if (clazz == Integer.class) {
            buf.put(EBON.C_INT);
            buf.putInt((((Integer) value)).intValue());
        } else if (clazz == Long.class) {
            buf.put(EBON.C_LONG);
            buf.putLong((((Long) value)).longValue());
        } else if (clazz == Double.class) {
            buf.put(EBON.C_DOUBLE);
            buf.putDouble((((Double) value)).doubleValue());
        } else if (clazz == String.class) {
            buf.put(EBON.C_STRING);
            writeString((String) value);
        } else {
            buf.put(EBON.C_DOCUMENT);
            writeDocument(value);
        }
    }

}
