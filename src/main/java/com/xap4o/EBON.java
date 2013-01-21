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

public class EBON {
    public static final byte C_NULL = 0;
    public static final byte C_BOOLEAN = 1;
    public static final byte C_INT = 2;
    public static final byte C_LONG = 3;
    public static final byte C_DOUBLE = 4;
    public static final byte C_STRING = 5;
    public static final byte C_LIST = 6;
    public static final byte C_OBJECT = 7;
    public static final byte C_BINARY = 8;
    public static final byte C_MAP = 9;
    public static final byte C_ENUM= 10;
    public static final byte C_REF = 11;

    public static final String CLASSNAME_MAP_KEY = "$className";


    public static byte[] serialize(Object doc) {
        return new EBONSerializer().serialize(doc);
    }

    public static <T> T deserialize(byte[] bytes) {
        return (T) new EBONDeserializer().deserialize(bytes);
    }

    public static byte[] serializeFromMap(Object doc) {
        return new EBONSerializer(true).serialize(doc);
    }

    public static <T> T deserializeAsMap(byte[] bytes) {
        return (T) new EBONDeserializer(true).deserialize(bytes);
    }
}
