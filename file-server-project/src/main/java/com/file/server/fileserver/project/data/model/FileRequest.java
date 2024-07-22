package com.file.server.fileserver.project.data.model;

import com.file.server.fileserver.project.data.dto.FileDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class FileRequest {

    private FileDTO fileDTO;
    private String email;
    private File file;
}
