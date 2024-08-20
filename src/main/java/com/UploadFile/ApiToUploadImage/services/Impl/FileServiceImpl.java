package com.UploadFile.ApiToUploadImage.services.Impl;

import com.UploadFile.ApiToUploadImage.services.FileServices;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileServices {

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {  // exception aayaghi file copy hoona mein deekat ayaghi tho
        //file name
        String name = file.getOriginalFilename();

        // THis is used for random name generate file . for Ex : your file name is abc.png and then it generate like 234235_13453_9459.png
//        String randomID = UUID.randomUUID().toString();
//        String fileName1 = randomID.concat(name.substring(name.lastIndexOf(".")));

        //fullPath     // File.separator means /
        String filePath = path + File.separator +name;     // if you want random name so use here fileName1 at place of name

        //create folder if not created
        File f = new File(path);
        if(!f.exists()){
            f.mkdir();
        }

        //file copy
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return name;
    }

    @Override
    public InputStream getResource(String path, String fileName) throws FileNotFoundException {
      String fullPath = path+File.separator+fileName;
      InputStream is  = new FileInputStream(fullPath);

        return is;
    }
}
