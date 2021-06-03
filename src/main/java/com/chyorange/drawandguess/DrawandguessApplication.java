package com.chyorange.drawandguess;

import com.chyorange.drawandguess.controller.WebSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DrawandguessApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(DrawandguessApplication.class);
        ConfigurableApplicationContext run = springApplication.run(args);
        WebSocketServer.setApplicationContext(run);
    }

}
