/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.controller;

import com.parillume.external.chat.model.ChatTopic;
import com.parillume.model.CompanyModel;
import com.parillume.model.CorpusModel;
import com.parillume.external.email.service.EmailService;
import com.parillume.print.display.DiskImage;
import com.parillume.print.display.ImageIF;
import com.parillume.security.service.SessionService;
import com.parillume.template.TemplateService.Template;
import com.parillume.util.StringUtil;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@RestController
public class EmailController extends AbstractController {
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SessionService sessionService;
    
    /**
     * FOR TESTING PURPOSES
     * Dynamically adds a Parillume logo to the email
     */
    @PostMapping(path = "/sendemail")
    public ResponseEntity<String> sendEmail(@RequestParam(value = "template") Template template,
                                            @RequestParam(value = "to") String to,
                                            @RequestParam(value = "subject") String subject,
                                            @RequestBody Map<String, String> flagReplacements,
                                            @RequestParam(value = "sessionid") String sessionId)
    throws Exception {
        try {            
            sessionService.validateSession(sessionId);        
            
            DiskImage parillumeLogoImage = DiskImage.getImage(ImageIF.PARILLUME_LOGO);
            String imageStr = Base64.getEncoder().encodeToString( parillumeLogoImage.getImageStream().readAllBytes() );
            flagReplacements.put("parillumeLogo", imageStr);
            
            emailService.sendEmail(template, to, subject, flagReplacements);
            return createResponse("Email sent to " + to);  
            
        } catch(Exception exc) {            
            return createResponse("Failed to send email", exc);
        } 
    }
}
