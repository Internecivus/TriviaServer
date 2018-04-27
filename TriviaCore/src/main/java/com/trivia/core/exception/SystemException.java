package com.trivia.core.exception;

import javax.ejb.ApplicationException;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
public class SystemException extends BusinessException {
    public SystemException() {
        super();
    }

    public SystemException(String message) {
        super(message);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}