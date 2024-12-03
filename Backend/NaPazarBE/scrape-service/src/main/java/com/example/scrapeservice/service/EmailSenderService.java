package com.example.scrapeservice.service;

public interface EmailSenderService {

    void sendEmail(String toEmail, String body, String subject);
}
