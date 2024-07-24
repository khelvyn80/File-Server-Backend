package com.file.server.fileserver.project.service;



import com.amazonaws.services.s3.model.AmazonS3Exception;

import com.file.server.fileserver.project.data.dto.FileDTO;
import com.file.server.fileserver.project.data.model.FileEntity;
import com.file.server.fileserver.project.data.model.FileRequest;
import com.file.server.fileserver.project.data.model.FileType;
import com.file.server.fileserver.project.exceptions.NotFoundException;
import com.file.server.fileserver.project.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;



import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileRepository fileRepository;
    private final String uploadDir = "C:/Users/Ibironke Allen/OneDrive/Documents/kelvin/File-Server-Backend/fileUploads";
    private final EmailService emailService;
    private final S3Client s3Client;
    private final String bucketName = "kelvinsfileserverbucket";


    public List <FileEntity> getAllFiles(){
        return this.fileRepository.findAll();
    };

    public FileEntity getFileByTitle(String title) throws FileNotFoundException {
        var file = this.fileRepository.findFileEntitiesByTitle(title);

        if (file.isEmpty()){
            throw new FileNotFoundException("File does not exist");
        }

        return file.get();
    }

    public FileEntity uploadFile(FileDTO fileDTO) throws Exception {
        try {
            MultipartFile fileToUpload = fileDTO.getFile();
            Path directory = Paths.get(uploadDir);
            String prefix = String.valueOf(System.currentTimeMillis()+"_");
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            // Save the file entity details
            var file = new FileEntity();
            file.setTitle(fileDTO.getTitle());
            file.setFileType(fileDTO.getFileType());
            file.setDescription(fileDTO.getDescription());
            file.setFilePath(prefix);
            file.setDateUploaded(Date.valueOf(LocalDate.now()));
            file.setFileName(fileToUpload.getOriginalFilename());
            file.setNumDownloads(0);
            file.setNumEmails(0);

            // Save it in the directory
            Path filePath = directory.resolve(fileToUpload.getOriginalFilename());
            log.info("File Path: {}", filePath);
            Files.write(filePath, fileToUpload.getBytes());


            this.s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(prefix+fileToUpload.getOriginalFilename())
                    .build(), RequestBody.fromBytes(fileToUpload.getBytes()));

            return this.fileRepository.save(file);
        } catch (IOException e) {
            throw new IOException("Failed to upload file: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("Error Message: " + e.getMessage());
        }
    }


    public List <FileEntity> getFilesByFileType(FileType fileType){
        return this.fileRepository.findFileEntitiesByFileType(fileType);
    }

    public Resource downloadFile(String filename) throws Exception {
        try {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName).key(filename).build();

            URI uri = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(filename)).toURI();

            Resource resource = new UrlResource(uri.toURL());

            log.info("Retrieved file from S3 bucket: {}", filename);
            log.info("Retrieved url from S3 bucket: {}", uri.toURL().toString());

            return resource;
        } catch (AmazonS3Exception e) {
            log.info("Error from S3 AMAZON: {}",e.getMessage());
            throw new NotFoundException("File not found in S3 bucket");
        }
        catch (Exception e) {
            log.info("Last Error : {}", e.getMessage());
            throw new Exception("Error Message: " + e.getMessage());
        }
    }

    private File getFileToSend(String filename){
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            if (!Files.exists(filePath)){
                throw new FileNotFoundException("File does not exist: "+filename);
            }
            return new File(filePath.toFile().getAbsolutePath());

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private FileEntity getFileByName(String filename) throws FileNotFoundException {
        if (this.fileExists(filename)){
            return this.fileRepository.findFileEntitiesByFileName(filename).get();
        }
        else {
            throw new FileNotFoundException("File does not exist");
        }
    }


    private boolean fileExists(String filename){
        return this.fileRepository.findFileEntitiesByFileName(filename).isPresent();
    }

    public String deleteFile(String filename) {
        try {
            if (this.fileExists(filename)) {

                Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
                log.info("Delete file path : {}", filePath);
                if (Files.exists(filePath)) {
                    this.fileRepository.delete(this.getFileByName(filename));
                    Files.delete(filePath);

                    return "File deleted successfully : " + filename;
                } else {
                    throw new NotFoundException("File does not exist,  " + filename);
                }

            }

        } catch (IOException e) {
            return "Error message: " + e.getMessage();
        }
        return null;
    }

    public String sendFileToEmail(String filename, String email) throws FileNotFoundException {
        try {
            var file = this.getFileByName(filename);
            FileDTO fileDTO =  new FileDTO();
            fileDTO.setTitle(file.getTitle());
            fileDTO.setDescription(file.getDescription());
            fileDTO.setFileType(file.getFileType());

            FileRequest fileRequest = new FileRequest();
            fileRequest.setEmail(email);
            fileRequest.setFileDTO(fileDTO);
            fileRequest.setFile(this.getFileToSend(filename));

            log.info("File has been sent to email successfully");
            this.emailService.sendFileToUser(fileRequest);
            file.setNumEmails(file.getNumEmails()+1);
            this.fileRepository.save(file);
            return "success";
        }

        catch (Exception e){
            throw new FileNotFoundException(e.getMessage());
        }

    }



}
