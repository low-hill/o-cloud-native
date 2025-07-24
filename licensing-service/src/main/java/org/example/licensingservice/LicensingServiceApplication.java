package org.example.licensingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class LicensingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LicensingServiceApplication.class, args);
    }

}
