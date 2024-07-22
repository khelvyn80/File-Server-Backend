package com.file.server.fileserver.project.service;


import com.file.server.fileserver.project.data.dto.FileDTO;
import com.file.server.fileserver.project.data.model.FileEntity;
import com.file.server.fileserver.project.data.model.FileType;
import com.file.server.fileserver.project.exceptions.NotFoundException;
import com.file.server.fileserver.project.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
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
    private String uploadDir = "C:/Users/Ibironke Allen/OneDrive/Documents/kelvin/File-Server-Backend/fileUploads";

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

    public FileEntity uploadFile(FileDTO fileEntity) throws Exception {

        try {
            MultipartFile fileToUpload = fileEntity.getFile();
            Path directory = Paths.get(uploadDir);
            if (!Files.exists(directory)){
                Files.createDirectories(directory);
            }

            //Save the file entity details
            var file = new FileEntity();
            file.setTitle(fileEntity.getTitle());
            file.setFileType(fileEntity.getFileType());
            file.setDescription(file.getDescription());
            file.setDateUploaded(Date.valueOf(LocalDate.now()));
            file.setFileName(fileEntity.getFile().getOriginalFilename());

            // Save it in the directory
            Path filePath = directory.resolve(fileToUpload.getOriginalFilename());
            log.info("File Path : {}",filePath);
            Files.write(filePath, fileToUpload.getBytes());

            return this.fileRepository.save(file);
        }
        catch (IOException e){
            throw new IOException("Failed to upload file: " + e.getMessage());
        }

        catch (Exception e){
            throw new Exception("Error Message :"+e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteFile(String title) throws FileNotFoundException {
        var file = this.fileRepository.findFileEntitiesByTitle(title)
                .orElseThrow(() -> new FileNotFoundException("File does not exist"));
        this.fileRepository.delete(file);
    }

    public List <FileEntity> getFilesByFileType(FileType fileType){
        return this.fileRepository.findFileEntitiesByFileType(fileType);
    }

    public Resource downloadFile(String filename) throws Exception {
        try {
            System.out.println(this.fileExists(filename));
            if (this.fileExists(filename)){
                Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
                log.info("Download file path : {}", filename);
                Resource resource = new UrlResource(filePath.toUri());
                if (!resource.exists() || !resource.isReadable()){
                    throw new NotFoundException("File does not exist");
                }

                Optional<FileEntity> optionalFile = this.fileRepository.findFileEntitiesByFileName(resource.getFilename());
                if (optionalFile.isPresent()) {
                    FileEntity file = optionalFile.get();
                    file.setNumDownloads(file.getNumDownloads()+1);
                    this.fileRepository.save(file);
                } else {
                    throw new NotFoundException("File metadata not found");
                }

                return resource;
            } else {
                throw new NotFoundException("File does not exist");
            }
        } catch (MalformedURLException e) {
            throw new MalformedURLException(e.getMessage());
        } catch (NotFoundException e) {
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    private boolean fileExists(String filename){
        return this.fileRepository.findFileEntitiesByFileName(filename).isPresent();
    }
}
