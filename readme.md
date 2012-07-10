EBON (efficient binary object notation)
=======================================

Library for serializing/deserializing object graphs in simple binary format (much like BSON but without MongoDb stuff).

This is very early draft. Specification is subject to change with no backward compatibility.

Actionscript implementation is [here](https://github.com/mkulak/ebon-as).

Spec:
TODO

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

Limitations:
* Serialized objects must not contain array fields (except byte[])
