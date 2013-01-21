EBON (efficient binary object notation)
=======================================
EBON is a binary data representation format for network transfer and data storage.
It was designed to represent graphs of simple [DTO](http://en.wikipedia.org/wiki/Data_transfer_object)-like objects
(much like BSON but without MongoDb stuff).


Spec: TODO

This is very early draft. Specification is subject to change with no backward compatibility.

This project is a java library for serializing/deserializing object graphs in EBON format.

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
