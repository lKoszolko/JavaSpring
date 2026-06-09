package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class CarRentApplication {
    public static void main(String[] args){
        SpringApplication.run(CarRentApplication.class, args);
    }
}
