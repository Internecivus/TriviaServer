package com.trivia.core.exception;


import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.NoResultException;
import java.io.Serializable;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
@Interceptor
public class ExceptionInterceptor implements Serializable {
    @AroundInvoke
    public Object handle(InvocationContext invocationContext) throws Exception {
        try {
            return invocationContext.proceed();
        }
        catch (javax.persistence.EntityNotFoundException | NoResultException e) {
            throw new EntityNotFoundException();
        }
        catch (javax.persistence.EntityExistsException e) {
            throw new EntityExistsException();
        }
        catch (IllegalStateException e) {
            throw new SystemException(e);
        }
//        catch (Exception e) {
//            throw new SystemException(e);
//        }
    }
}