package com.daniel.pods.model;

import java.net.URI;

import org.springframework.stereotype.Component;

@Component
public final class Vocabulary {
    public static String VCARD_BASE= "http://www.w3.org/2006/vcard/ns#";
    public static final String FN= VCARD_BASE.concat("fn");
    
    public static final URI PS_ACCESS_GRANT_URI = URI.create("https://vc.inrupt.com");

    public Vocabulary() {
    }
}
