/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.controller;

import com.parillume.print.display.DBImage;
import com.parillume.security.Session;
import com.parillume.security.service.PermissionsService;
import com.parillume.security.service.SessionService;
import com.parillume.service.ImageService;
import com.parillume.util.model.ImageType;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@RestController
public class ImageController extends AbstractController {
    
    @Autowired
    private ImageService imageService;
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private PermissionsService permissionsService;

    @GetMapping("/getimage")
    public ResponseEntity<String> getImage(@RequestParam(value = "companyid") String companyId,
                                           @RequestParam(value = "imagetype") ImageType imageType) {
        try {
            DBImage image = imageService.getImage(companyId, imageType);
            if(image == null)
                throw new Exception("Image not found");

            return createResponse( Base64.getEncoder().encodeToString(image.getImageBytes()) );
        } catch(Exception exc) {
            return createResponse("");
        }
    }

    @PostMapping("/upsertimage")
    public ResponseEntity<String> upsertImage(@RequestParam("files") List<MultipartFile> files,
                                              @RequestParam(value = "companyid") String companyId,
                                              @RequestParam(value = "imagetype") ImageType imageType,
                                              @RequestParam(value = "sessionid") String sessionId) 
    throws IOException {
        try {
            if(files.size() > 1)
                throw new Exception("Please upload a single logo image");
            
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "upsertimage"); 

            MultipartFile imageFile = files.get(0);
            
            DBImage preexistingImage = imageService.getImage(companyId, imageType);
            DBImage image = new DBImage(companyId, imageFile.getOriginalFilename(), imageType, 
                                        imageFile.getBytes(),
                                        preexistingImage != null ? preexistingImage.getId() : null);            

            imageService.upsertImage(image);
            
            return createResponse(imageType + " image saved for company " + companyId);
        } catch(Exception exc) {
            return createResponse("Failed to " + imageType + " image for company " + companyId, exc);
        }
    }
    
    @GetMapping(path = "/deleteimage")
    public ResponseEntity<String> deleteUsers(@RequestParam(value = "imageid") Long imageId,
                                              @RequestParam(value = "sessionid") String sessionId) {
        try {
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "deleteimage"); 
            
            imageService.deleteImage(imageId);
            return createResponse("Image deleted");
        } catch(Exception exc) {            
            return createResponse("Failed to delete image " + imageId, exc);
        }          
    }    
}
