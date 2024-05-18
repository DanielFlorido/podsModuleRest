package com.daniel.pods.controllers;

import com.daniel.pods.service.UserService;
import com.inrupt.client.solid.SolidSyncClient;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;

@RestController
public class HomeController {

    @Autowired
    private UserService userService;
    @Autowired
    private SolidSyncClient client;
    private final PrintWriter printWriter = new PrintWriter(System.out, true);
    @GetMapping("/")
    public String home(){
        if(userService.getCurrentUser()!= null){
            if(client==null){
                printWriter.println("client null");
            }
            printWriter.println("username: "+userService.getCurrentUser().geUserName());
            return ("username: "+userService.getCurrentUser().getToken());
        }
        return "helloSecured!";
    }
    @GetMapping("/login")
    public String login(){
        if(userService.getCurrentUser()!= null){
            printWriter.println("username: "+userService.getCurrentUser().geUserName());
            return ("username: "+userService.getCurrentUser().geUserName());

        }
        return "helloSecured!";
    }
}
