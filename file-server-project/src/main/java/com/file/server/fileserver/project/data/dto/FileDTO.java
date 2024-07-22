package com.file.server.fileserver.project.data.dto;

import com.file.server.fileserver.project.data.model.FileEntity;
import com.file.server.fileserver.project.data.model.FileType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileDTO {
    private String title;
    private String description;
    private FileType fileType;
    private MultipartFile file;

}
