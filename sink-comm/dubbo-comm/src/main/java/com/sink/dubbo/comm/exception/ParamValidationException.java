package com.sink.dubbo.comm.exception;

/**
 *
 * @author Iven
 * @date 2015-02-03
 */
public class ParamValidationException extends PlatformException {

    public ParamValidationException(String message) {
        super(message);
    }

    public ParamValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamValidationException(Throwable cause) {
        super(cause);
    }

}
