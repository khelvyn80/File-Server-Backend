package com.file.server.fileserver.project.controller;

import com.file.server.fileserver.project.data.dto.FileDTO;
import com.file.server.fileserver.project.data.model.FileEntity;
import com.file.server.fileserver.project.data.model.FileType;
import com.file.server.fileserver.project.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final FileService fileService;

    @PostMapping("/upload")
    public String upload(@RequestParam("title") String title,
                         @RequestParam("description") String description,
                         @RequestParam("fileType") FileType fileType,
                         @RequestParam("file") MultipartFile file) throws Exception{

        FileDTO fileDTO = new FileDTO();
        fileDTO.setTitle(title);
        fileDTO.setDescription(description);
        fileDTO.setFileType(fileType);
        fileDTO.setFile(file);
        var fileEntity = this.fileService.uploadFile(fileDTO);

        return "";
    }

    @GetMapping("/dashboard")
    @ResponseBody
    public ResponseEntity<List<FileEntity>> dashboard(){
        try {
            List<FileEntity> fileEntityList = this.fileService.getAllFiles();
            return ResponseEntity.ok(fileEntityList);
        }
        catch (Exception e){
            throw new RuntimeException("Error Message: "+e.getMessage());
        }
    }
}
