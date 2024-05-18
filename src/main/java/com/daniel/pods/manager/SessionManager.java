package com.daniel.pods.manager;

import com.daniel.pods.service.UserService;
import com.inrupt.client.auth.Session;
import com.inrupt.client.openid.OpenIdSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class SessionManager {

    private Session session;

    @Autowired
    private UserService userService;

    public Session getSession() {
        if(session==null){
            session = OpenIdSession.ofIdToken(userService.getCurrentUser().getToken());
        }
        return session;
    }
}
