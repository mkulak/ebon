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
    private Map<Integer, Object> refMap = new HashMap<Integer, Object>();

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
                return readStringImpl();
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
            case EBON.C_REF:
                return readRef();
        }
        throw new EBONException("Unsupported type: " + valType);
    }

    private Object readRef() {
        int ref = buf.getInt();
        Object res = refMap.get(ref);
        if (res == null) {
            throw new EBONException("Malformed ref " + ref);
        }
        return res;
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
        int ref = buf.getInt();
        String className = readString();
        int fieldsCount = buf.getInt();
        Object res = Reflector.newInstance(className);
        refMap.put(ref, res);
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
        byte valType = buf.get();
        switch (valType) {
            case EBON.C_STRING:
                return readStringImpl();
            case EBON.C_REF:
                return (String) readRef();
            default:
                throw new EBONException("Expected string, found " + valType);
        }
    }

    private String readStringImpl() {
        int ref = buf.getInt();
        int size = buf.getInt();
        byte[] bytes = new byte[size];
        buf.get(bytes);
        String res;
        try {
            res = new String(bytes, "UTF-8");
        } catch (Exception e) {
            throw new EBONException("", e);
        }
        refMap.put(ref, res);
        return res;
    }

    private byte[] readByteArray() {
        int ref = buf.getInt();
        int size = buf.getInt();
        byte[] bytes = new byte[size];
        buf.get(bytes);
        refMap.put(ref, bytes);
        return bytes;
    }

    private Map<Object, Object> readMap() {
        int ref = buf.getInt();
        int size = buf.getInt();
        Map<Object, Object> res = new HashMap<Object, Object>();
        refMap.put(ref, res);
        for (int i = 0; i < size; i++) {
            Object key = readValue();
            res.put(key, readValue());
        }
        return res;
    }

    private List readList() {
        int ref = buf.getInt();
        int size = buf.getInt();
        List res = new ArrayList();
        refMap.put(ref, res);
        for (int i = 0; i < size; i++) {
            res.add(readValue());
        }
        return res;
    }
}
