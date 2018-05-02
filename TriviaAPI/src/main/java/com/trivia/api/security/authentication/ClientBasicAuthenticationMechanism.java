package com.trivia.api.security.authentication;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.*;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.List;




@ApplicationScoped
public class ClientBasicAuthenticationMechanism implements HttpAuthenticationMechanism {
    @Inject private IdentityStore identityStore;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext context) {
        Credential credential = getCredential(request);

        if (credential != null) {
            CredentialValidationResult credentialValidationResult = identityStore.validate(credential);
            if (credentialValidationResult.getStatus() == CredentialValidationResult.Status.VALID) {
                context.notifyContainerAboutLogin(credentialValidationResult);
            }
        }

        response.setHeader("WWW-Authenticate","Basic realm=\"Trivia API\"");
        return context.responseUnauthorized();
    }

    private Credential getCredential(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            try {
                String[] headerCredential = new String(Base64.getDecoder().decode(authorizationHeader.substring(6))).split(":");
                return new UsernamePasswordCredential(headerCredential[0], headerCredential[1]);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                return null;
            }
        }
        return null;
    }
}