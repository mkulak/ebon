EBON (elementary binary object notation) - format which describes binary encoded documents (much like BSON but without MongoDb stuff).
This is very early draft. Specification is subject to change with no backward compatibility.

Spec:
TODO

Serialization/deserialization is that simple:
        MyObject obj = ...
        byte[] bytes = EBON.serialize(obj);
        MyObject newObj = EBON.deserialize(bytes);

Limitations:
        Serialized objects may contain only boolean/int/long/double/String/Map/List fields
        All maps should contain String as a key
        No arrays support (except byte[])
