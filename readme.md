EBON (efficient binary object notation)
=======================================
EBON is a binary data representation format for network transfer and data storage.
It was designed to represent graphs of simple [DTO](http://en.wikipedia.org/wiki/Data_transfer_object)-like objects
(much like BSON but without MongoDb stuff).


EBON specification:

    Terminals
        byte        1 byte (8-bits)
        int32       4 bytes (32-bit signed integer) big-endian
        int64       8 bytes (64-bit signed integer) big-endian
        double64	8 bytes (64-bit IEEE 754 floating point)

    Non-terminals
        entity       ::= null | boolean | int | long | double | string | list | map | object | byte_array | ref
        null         ::= \x00                             Null
        boolean      ::= \x01 \x01 | \x01 \x00            Boolean (1 for 'true' and 0 for 'false')
        int          ::= \x02 int32                       Integer
        long         ::= \x03 int64                       Long integer
        double       ::= \x04 double64                    Double precision floating point value
        string       ::= ref | \x05 int32 (byte*)         UTF-8 encoded string (int32 represents number of bytes in array)
        byte_array   ::= ref | \x08 int32 (byte*)         Array of bytes
        list         ::= ref | \x06 int32 (entity*)       List (int32 represents number of elements)
        map          ::= ref | \x09 int32 (key_value*)    Map (int32 represents number of key-value pairs)
        key_value    ::= entity entity                    Entity-key and entity-value
        object       ::= ref | \x07 int32 (named_entity*) Object (int32 represents number of fields)
        named_entity ::= string entity                    Name and entity
        ref          ::= \x11 int32                       Reference to already serialized object

This is very early draft. Specification is subject to change with no backward compatibility.

This project is a java library for serializing/deserializing object graphs in EBON format.
Library is released under [apache-2.0](http://www.apache.org/licenses/LICENSE-2.0) license.

To include ebon for java as maven dependency:

    <repository>
       <id>ebon-repo</id>
       <url>https://github.com/mkulak/ebon/raw/master/releases</url>
    </repository>

    <dependency>
        <groupId>com.xap4o</groupId>
        <artifactId>ebon</artifactId>
        <version>0.7</version>
    </dependency>

Serialization/deserialization is that simple:

    class MyObjectTree {
        public int a;
        private boolean b;
        private Map<String, List<MyCustomClass>> foo;
        @Skip
        private String secretField;

        @Getter("calculated-field-name")
        public int calcBar() { .... }

        @Setter("some-other-name")
        public void setBaz(long value) { .... }
    }

    MyObjectTree obj = ...
    byte[] bytes = EBON.serialize(obj);
    MyObjectTree newObj = EBON.deserialize(bytes);


Features:
* dead simple spec and API
* no external dependencies (just standard library)
* can serialize graphs
* java/actionscript support
* no dinosaur legacy code
* no Serializable or any other mandatory interface
* declaring default constructors is not necessary

Limitations:
All serialized objects should contain only fields of type boolean, int, long, double, String, List, Map, byte[], enum and
can contain objects of custom classes which themself comply with same restrictions. So byte, char, short, float, Set
and arrays of any type are not supported. This is purely design decision in order to make lib more simple, clean and cross-language.
Also current version depends on sun.reflect.ReflectionFactory which is internal class of Oracle JVM and may not be available on other JVMs.

Actionscript 3 implementation is [here](https://github.com/mkulak/ebon-as).
