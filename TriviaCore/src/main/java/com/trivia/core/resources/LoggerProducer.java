package com.trivia.core.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.ejb.Singleton;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */

@Dependent
public class LoggerProducer {
    @Produces
    public Logger getLogger(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }
}