package com.file.server.fileserver.project.repository;

import com.file.server.fileserver.project.data.model.FileEntity;
import com.file.server.fileserver.project.data.model.FileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity,Long> {
    List<FileEntity> findFileEntitiesByFileType(FileType fileType);
    Optional<FileEntity> findFileEntitiesByTitle(String title);
    Optional<FileEntity> findFileEntitiesByFileName(String fileName);

}
