package com.sink.dubbo.comm.exception;

public abstract class PlatformException extends Exception {
    protected String coder;

    private String getCoder(){
        return coder;
    }

    private String setCoder(){
        return coder;
    }

    public PlatformException(String message) {
        super(message);
    }

    public PlatformException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlatformException(Throwable cause) {
        super(cause);
    }
}
