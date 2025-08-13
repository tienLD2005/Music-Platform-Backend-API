package com.ra.base_spring_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@SpringBootApplication
public class BaseSpringBootApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(BaseSpringBootApplication.class, args);
    }
}
