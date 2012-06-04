package com.xap4o;

public class EBONException extends RuntimeException {
    public EBONException(String s) {
        super(s);
    }

    public EBONException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
