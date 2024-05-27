package com.daniel.pods.service;

import java.net.URI;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.inrupt.client.webid.WebIdProfile;

@SessionScope
@Service
public class PodsService {
    @Autowired 
    private UserService userService;

    public Set<URI> getStorage(){
        try (final var profile = userService.getClient().read(URI.create(userService.getCurrentUser().geUserName()), WebIdProfile.class)) {
            return profile.getStorages();
        }
    }
    public String getStringStorage(){
        Set<URI> storages =getStorage();
        String storageString= "";
        for (URI uri : storages) {
            storageString+= uri.toString();
        }
        return storageString;
    }
}
