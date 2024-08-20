package com.UploadFile.ApiToUploadImage.controllers;

import com.UploadFile.ApiToUploadImage.payload.FileResponse;
import com.UploadFile.ApiToUploadImage.services.FileServices;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileServices fileServices;

    @Value("@{project.image}")
    private String path;

    @PostMapping("/upload")
    public ResponseEntity<FileResponse> uploadImage(@RequestParam("image") MultipartFile image){
        String filename = null;
        try {
            filename = fileServices.uploadImage(path,image);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<FileResponse>(new FileResponse(null,"Image is not uploaded due to server error"),HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<FileResponse>(new FileResponse(filename,"Image is uploaded successfully"),HttpStatus.OK);
    }

    //method to serve files
    @GetMapping(value = "images/{imageName}",produces = MediaType.IMAGE_JPEG_VALUE)
    public void downloadImage(@PathVariable("imageName" ) String imageName, HttpServletResponse response) throws IOException {
        InputStream resource =  fileServices.getResource(path,imageName);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource,response.getOutputStream());

    }
}
