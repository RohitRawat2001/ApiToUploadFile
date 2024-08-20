Certainly! Let's go through the code step by step, explaining each part in detail.

# 1. FileServices Interface
```java
public interface FileServices {
    String uploadImage(String path, MultipartFile multipartFile) throws IOException;
    InputStream getResource(String path, String fileName) throws FileNotFoundException;
}
```

Purpose: This interface defines two methods related to file operations:
uploadImage: For uploading an image file.
getResource: For retrieving an image file from the server.
Loose Coupling: By using an interface, we can implement different versions of file services without changing the rest of the application. This promotes loose coupling.

# 2. FileServiceImpl Class
```java
@Service
public class FileServiceImpl implements FileServices {
```
@Service Annotation: Marks this class as a service in the Spring framework, making it a candidate for dependency injection.
Implements: This class provides the actual implementation for the FileServices interface.

# uploadImage Method
```java
@Override
public String uploadImage(String path, MultipartFile file) throws IOException {
    String name = file.getOriginalFilename();
    String filePath = path + File.separator + name;

    File f = new File(path);
    if (!f.exists()) {
        f.mkdir();
    }

    Files.copy(file.getInputStream(), Paths.get(filePath));

    return name;
}
```
@Override: Indicates that this method is implementing the method declared in the FileServices interface.
String name = file.getOriginalFilename();: Extracts the original file name from the uploaded file.
String filePath = path + File.separator + name;: Constructs the full path where the image will be stored. File.separator ensures the correct file separator is used depending on the operating system.
File f = new File(path);: Creates a File object representing the directory where the image will be saved.
if (!f.exists()) { f.mkdir(); }: Checks if the directory exists; if not, it creates the directory.
Files.copy(file.getInputStream(), Paths.get(filePath));: Copies the contents of the uploaded file to the specified location on the server.
return name;: Returns the name of the uploaded file.

# getResource Method
```java
@Override
public InputStream getResource(String path, String fileName) throws FileNotFoundException {
    String fullPath = path + File.separator + fileName;
    InputStream is = new FileInputStream(fullPath);

    return is;
}
```

Construct Full Path: String fullPath = path + File.separator + fileName; constructs the full path to the file using the directory path and the file name.
InputStream is = new FileInputStream(fullPath);: Opens a file input stream to read the file from the disk.
Return InputStream: The method returns the InputStream, which can be used to read the file's contents.

# 4. FileController Class
```java
@RestController
@RequestMapping("/file")
public class FileController {
```
@RestController Annotation: Marks the class as a REST controller, meaning it will handle HTTP requests and return responses.
@RequestMapping("/file"): Specifies that all URLs starting with /file will be handled by this controller.
Autowiring FileServices
```java
@Autowired
private FileServices fileServices;
```
@Autowired: Automatically injects an instance of FileServices (in this case, FileServiceImpl) into the controller, so you can use it without manually instantiating it.

# Injecting Path Configuration
```java
@Value("@{project.image}")
private String path;
```
@Value("@{project.image}"): Injects the value of project.image from the application.properties file into the path variable. This is the directory where images will be stored.

# uploadImage Method
```java
@PostMapping("/upload")
public ResponseEntity<FileResponse> uploadImage(@RequestParam("image") MultipartFile image) {
    String filename = null;
    try {
        filename = fileServices.uploadImage(path, image);
    } catch (IOException e) {
        e.printStackTrace();
        return new ResponseEntity<FileResponse>(new FileResponse(null, "Image is not uploaded due to server error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<FileResponse>(new FileResponse(filename, "Image is uploaded successfully"), HttpStatus.OK);
}
```
@PostMapping("/upload"): Handles HTTP POST requests at the /file/upload endpoint.
@RequestParam("image") MultipartFile image: Extracts the uploaded file from the request. The file is expected to be passed with the parameter name "image".
String filename = null;: Initializes a variable to hold the uploaded file name.
Try-Catch Block: Attempts to upload the file using the fileServices.uploadImage method. If an exception occurs, it returns a 500 Internal Server Error response with a failure message.
Return Response: On success, it returns a FileResponse object with the file name and a success message, along with a 200 OK status.

# downloadImage 

```java
@GetMapping(value = "images/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
public void downloadImage(@PathVariable("imageName") String imageName, HttpServletResponse response) throws IOException {
    InputStream resource = fileServices.getResource(path, imageName);
    response.setContentType(MediaType.IMAGE_JPEG_VALUE);
    StreamUtils.copy(resource, response.getOutputStream());
}
```
@GetMapping(value = "images/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE): Handles HTTP GET requests to download images. The {imageName} part of the URL is a placeholder for the actual image name.
@PathVariable("imageName") String imageName: Extracts the image name from the URL.
Fetch Image Resource: Calls fileServices.getResource(path, imageName) to get the image file as an InputStream.
Set Content Type: Sets the content type of the response to MediaType.IMAGE_JPEG_VALUE, indicating that the response contains a JPEG image.
Stream the Image: StreamUtils.copy(resource, response.getOutputStream()); writes the image data to the response output stream, sending the image back to the client.

# 5. FileResponse Class
```java
public class FileResponse {
    private String fileName;
    private String message;

    public FileResponse(String fileName, String message) {
        this.fileName = fileName;
        this.message = message;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```
Fields:
fileName: Stores the name of the file that was uploaded or requested.
message: Stores a message indicating the result of the operation (success or failure).
Constructor: Initializes the fileName and message fields.
Getters and Setters: Standard methods to retrieve and modify the values of fileName and message.

# 6. Configuration in application.properties
```properties
spring.application.name=ApiToUploadImage
spring-servlet.multipart.max-file-size=10MB
spring-servlet.multipart.max-request-size=10MB
project.image = images/
```
spring.application.name=ApiToUploadImage: Sets the name of the Spring Boot application.
File Upload Size Limits:
spring-servlet.multipart.max-file-size=10MB: Limits the maximum file size that can be uploaded to 10MB.
spring-servlet.multipart.max-request-size=10MB: Limits the maximum request size (including all files and form data) to 10MB.
project.image = images/: Specifies the directory where uploaded images will be stored.

# Summary

The FileServices interface defines the contract for file operations.
FileServiceImpl provides the actual implementation for uploading and fetching images.
FileController handles HTTP requests for uploading and downloading images.
FileResponse is used to structure the response after file operations.
The application.properties file contains configuration settings, such as file size limits and the storage directory for images.
