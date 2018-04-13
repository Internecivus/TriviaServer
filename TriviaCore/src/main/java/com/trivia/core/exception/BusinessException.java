package com.trivia.core.exception;

import javax.ejb.ApplicationException;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
@ApplicationException(rollback = true)
public abstract class BusinessException extends RuntimeException {
    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
