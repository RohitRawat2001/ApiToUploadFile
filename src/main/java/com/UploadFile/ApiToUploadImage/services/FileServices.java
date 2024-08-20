package com.UploadFile.ApiToUploadImage.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

// This File is made for loose coupling
public interface FileServices {

    // upload Images
    String uploadImage(String path, MultipartFile multipartFile) throws IOException;

    //serve Images
    InputStream getResource(String path,String fileName) throws FileNotFoundException;
}
