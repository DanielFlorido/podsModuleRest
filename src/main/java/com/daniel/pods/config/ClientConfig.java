package com.daniel.pods.config;

import com.inrupt.client.solid.SolidSyncClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ClientConfig {
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SolidSyncClient solidClient(){
        return SolidSyncClient.getClient();
    }
}
