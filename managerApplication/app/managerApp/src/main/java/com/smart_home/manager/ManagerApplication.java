package com.smart_home.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ManagerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ManagerApplication.class, args);
  }

}
