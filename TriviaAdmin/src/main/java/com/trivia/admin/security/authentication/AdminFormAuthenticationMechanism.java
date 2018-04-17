package com.trivia.admin.security.authentication;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.*;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.IdentityStore;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
//@AutoApplySession // For "Is user already logged-in?"
//@RememberMe(
//        cookieSecureOnly = true,
//        cookieMaxAgeSeconds = 60 * 60 * 24 * 7) // 7 days.
//@LoginToContinue(
//        loginPage = "/login?continue=true",
//        errorPage = "",
//        useForwardToLogin = false)
//@ApplicationScoped
//public class AdminFormAuthenticationMechanism implements HttpAuthenticationMechanism {
//    @Inject private IdentityStore identityStore;
//
//    @Override
//    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext context) {
//        Credential credential = context.getAuthParameters().getCredential();
//
//        if (credential != null) {
//            return context.notifyContainerAboutLogin(identityStore.validate(credential));
//        }
//        else {
//            return context.doNothing();
//        }
//    }
//}