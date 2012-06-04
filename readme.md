EBON (e binary object notation) - format which describes binary encoded documents (much like BSON but without MongoDb stuff).

document = type list
list = element list | nothing
element = name \x01 boolean
        | name \x02 int
        | name \x03 long
        | name \x04 double
        | name \x05 string
        | name \x06 array
        | name \x07 document
        | name \x08 bytes
        | ref  \x09

int = <4 big-endian bytes>
name = string
string = int <utf-8 encoded bytes>
array = int <elements>
bytes = int <bytes>
ref = int
