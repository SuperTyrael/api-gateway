package org.brown.mliang21.backend.http.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.brown.mliang21")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
