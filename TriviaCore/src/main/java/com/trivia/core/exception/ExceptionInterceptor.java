package com.trivia.core.exception;


import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
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
        catch (javax.persistence.EntityNotFoundException e) {
            throw new EntityNotFoundException();
        }
        catch (javax.persistence.EntityExistsException e) {
            throw new EntityExistsException();
        }
        catch (javax.persistence.NoResultException e) {
            throw new NoResultException();
        }
        catch (java.security.NoSuchAlgorithmException e) {
            throw new SystemException();
        }
    }
}