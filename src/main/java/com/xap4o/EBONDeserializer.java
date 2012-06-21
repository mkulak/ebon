package com.xap4o;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
            case EBON.C_OBJECT:
                return readObject();
            case EBON.C_ENUM:
                return readEnum();
        }
        throw new EBONException("Unsupported type: " + valType);
    }

    private Object readEnum() {
        String clazzName = readString();
        String name = readString();
        try {
            Class enumClass = Class.forName(clazzName);
            return Enum.valueOf(enumClass, name);
        } catch (ClassNotFoundException e) {
            throw new EBONException("No enum class " + clazzName);
        }
    }

    private Object readObject() {
        String className = readString();
        int fieldsCount = buf.getInt();
        Object res = Reflector.newInstance(className);
        Map<String, Field> name2field = Reflector.getFields(res.getClass());
        Map<String, Method> name2setter = Reflector.getSetters(res.getClass());
        for (int i = 0; i < fieldsCount; i++) {
            String name = readString();
            Object value = readValue();
            try {
                Method method = name2setter.get(name);
                Field field = name2field.get(name);
                if (method != null) {
                    method.invoke(res, value);
                } else if (field != null) {
                    field.set(res, value);
                } else {
                    throw new EBONException("Cannot set field " + name + " for clazz " + className);
                }
            } catch (EBONException e) {
                throw e;
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

    private Map<Object, Object> readMap() {
        int size = buf.getInt();
        Map<Object, Object> res = new HashMap<Object, Object>();
        for (int i = 0; i < size; i++) {
            Object key = readValue();
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
