/*
 * Copyright 2013 Michael Kulak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xap4o;

import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Reflector {
    public static Object newInstance(String className) {
        try {
            Class<?> cls = Class.forName(className);
            Constructor objCon = Object.class.getDeclaredConstructor();
            Constructor con = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(cls, objCon);
            return con.newInstance();
        } catch (Exception e) {
            throw new EBONException("Cannot create new instance of " + className, e);
        }
    }

    public static Map<String, Field> getFields(Class clazz) {
        return getFields(clazz, new HashMap<String, Field>());
    }
    private static Map<String, Field> getFields(Class clazz, Map<String, Field> acc) {
        if (clazz == Object.class) {
            return acc;
        }
        for (Field f : clazz.getDeclaredFields()) {
            f.setAccessible(true);
            acc.put(f.getName(), f);
        }
        return getFields(clazz.getSuperclass(), acc);
    }

    public static Map<String, Method> getGetters(Class clazz) {
        return getGetters(clazz, new HashMap<String, Method>());
    }
    public static Map<String, Method> getGetters(Class clazz, Map<String, Method> acc) {
        if (clazz == Object.class) {
            return acc;
        }
        for (Method m : clazz.getDeclaredMethods()) {
            Getter an = m.getAnnotation(Getter.class);
            if (an != null) {
                m.setAccessible(true);
                acc.put(an.value(), m);
            }
        }
        return getGetters(clazz.getSuperclass(), acc);
    }

    public static Map<String, Method> getSetters(Class clazz) {
        return getSetters(clazz, new HashMap<String, Method>());
    }
    public static Map<String, Method> getSetters(Class clazz, Map<String, Method> acc) {
        if (clazz == Object.class) {
            return acc;
        }
        for (Method m : clazz.getDeclaredMethods()) {
            Setter an = m.getAnnotation(Setter.class);
            if (an != null) {
                m.setAccessible(true);
                acc.put(an.value(), m);
            }
        }
        return getGetters(clazz.getSuperclass(), acc);
    }
}
