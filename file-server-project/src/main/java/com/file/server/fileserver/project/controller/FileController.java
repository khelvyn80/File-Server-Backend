package com.file.server.fileserver.project.controller;

import com.file.server.fileserver.project.data.dto.FileDTO;
import com.file.server.fileserver.project.exceptions.NotFoundException;
import com.file.server.fileserver.project.repository.FileRepository;
import com.file.server.fileserver.project.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;


@RestController()
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/file")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestBody FileDTO fileDTO) throws Exception {
        var file = this.fileService.uploadFile(fileDTO);
        return ResponseEntity.ok("File uploaded successfully "+fileDTO.getFile().getOriginalFilename());
    }



    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws Exception {
        try {
            System.out.println("This is the beginning of the downloads");
            Resource resource = this.fileService.downloadFile(filename);
            System.out.println(resource.getFilename());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName){
        try {
            Path filePath =  Paths.get(uploadDir).resolve(fileName).normalize();
            log.info("delete file path : {}",filePath);
            if(Files.exists(filePath)){
                Files.delete(filePath);
                return ResponseEntity.ok("File deleted successfully: "+ fileName);
            }
            else   {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: "+ fileName);
            }

        }
        catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete file: "+e.getMessage());
        }
    }
    */
}
