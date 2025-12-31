/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.external.email.service;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */

import com.parillume.template.TemplateService;
import com.parillume.template.TemplateService.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import lombok.Data;

@Data
@Service
public class EmailService {
    
    @Autowired
    private TemplateService templateService;

    /**
     * See application.properties:spring.mail.*
     */
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private ResourceLoader resourceLoader;

    public void sendEmail(Template template,
                          String to, String subject, 
                          // String: {{flag} referenced in template HTML file
                          // Object: String text, String Base64 image, etc.
                          Map<String, String> flagReplacements) 
    throws Exception {
        String emailContent = templateService.processTemplate(template, flagReplacements);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(emailContent);
        emailSender.send(message);
    }
}
