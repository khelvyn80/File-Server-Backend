package com.file.server.fileserver.project.data.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Data
@Entity
public class FileEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long fileId;
    private String title;
    private String description;
    private String filePath;
    @Enumerated(EnumType.STRING)
    private FileType fileType;
    private String fileName;
    private Date dateUploaded;
    private int numDownloads;
    private int numEmails;

}
