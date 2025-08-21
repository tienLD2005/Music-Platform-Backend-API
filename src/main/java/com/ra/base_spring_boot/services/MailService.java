package com.ra.base_spring_boot.services;


public interface MailService {
    void sendEmail(String to, String subject, String body);
}
