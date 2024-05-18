package com.daniel.pods.service;

import com.daniel.pods.model.WebIdOwner;
import com.inrupt.client.solid.SolidSyncClient;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.net.URI;

@Service
@SessionScope
public class UserService {
    private final SolidSyncClient client = SolidSyncClient.getClient();

    public WebIdOwner getCurrentUser(){
        if(!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)){
            final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if(principal instanceof OidcUser){
                final OidcUser user= (OidcUser) principal;
                final String webidurl= user.getClaim("webid");
                try (final WebIdOwner profile = client.read(URI.create(webidurl), WebIdOwner.class)){
                    profile.setToken(user.getIdToken().getTokenValue());
                    return profile;
                }
            }
        }
        return null;
    }
}
