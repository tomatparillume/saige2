/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.template;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */

import com.parillume.util.StringUtil;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Service
public class TemplateService {

    public enum Template {
        AUTOCHAT_EMAIL("autoemail_template.html",
                       new String[]{"name","chatContent","parillumeLogo"});
        
        private String fileName;
        private String[] flags = new String[0];
        private Template(String fileName, String... flags) {
            this.fileName = fileName;
            this.flags = flags;
        }
        public String getFileName() {
            return fileName;
        }
        public String getResourcePath() {
            return "templates/" + getFileName();
        }
        
        public void validate(Collection<String> requestedFlags) throws Exception {
            List<String> expectedFlags = new ArrayList(Arrays.asList(flags));
            if( !StringUtil.collectionsEqual(expectedFlags, requestedFlags))
                throw new Exception("Request flags " + requestedFlags + " and " + name() + " flags " + flags + " do not match");
        }
    }          
    
    public String processTemplate(Template template, Map<String, String> flagReplacements) 
    throws Exception {
        template.validate(flagReplacements.keySet());   
        
        Resource resource = new ClassPathResource(template.getResourcePath());
        if(!resource.exists())
            throw new FileNotFoundException(template.getResourcePath() + " not found");

        Path path = resource.getFile().toPath();
        String templateStr = new String(Files.readAllBytes(path));
            
        // Replace all ${flag} placeholders with corresponding values from the map
        for (Map.Entry<String, String> entry : flagReplacements.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            String value = entry.getValue();
            templateStr = templateStr.replace(placeholder, value);
        }
        
        return templateStr;
    }       
}
