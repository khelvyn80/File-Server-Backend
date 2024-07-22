package com.file.server.fileserver.project.controller;

import com.file.server.fileserver.project.data.dto.FileDTO;
import com.file.server.fileserver.project.data.model.FileType;
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
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;


@RestController()
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/file")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("title") String title,
                                             @RequestParam("description") String description,
                                             @RequestParam("fileType") FileType fileType,
                                             @RequestParam("file")MultipartFile file) throws Exception {

        FileDTO fileDTO = new FileDTO();
        fileDTO.setTitle(title);
        fileDTO.setDescription(description);
        fileDTO.setFileType(fileType);
        fileDTO.setFile(file);
        var fileEntity = this.fileService.uploadFile(fileDTO);
        return ResponseEntity.ok("File uploaded successfully "+fileEntity.getFileName());
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

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName){
        try {
                String response = this.fileService.deleteFile(fileName);;
                return ResponseEntity.ok(response);
            }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete file: "+e.getMessage());
        }
    }

    @GetMapping("/send/{filename}")
    public ResponseEntity<String> sendToEmail(@PathVariable String filename, @RequestParam("email")String email){
        try {
            String response = this.fileService.sendFileToEmail(filename,email);
            if ("success".equalsIgnoreCase(response)){
                return ResponseEntity.ok("Email has been sent successfully");
            }
            else {
                return ResponseEntity.badRequest().body("Email failed to send");
            }

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
