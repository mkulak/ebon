EBON (efficient binary object notation)
=======================================

Library for serializing/deserializing object trees in simple binary format (much like BSON but without MongoDb stuff).

This is very early draft. Specification is subject to change with no backward compatibility.

At this time only java version is available. Actionscript will be supported soon.

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


Limitations:
* Serialized objects may contain only boolean/int/long/double/String/Map/List/enum fields
* All maps should contain String as a key
* No arrays support (except byte[])
