package com.example.demo.modules.file.repository;

import com.example.demo.modules.file.entity.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findByUploaderIdOrderByUploadedAtDesc(Long uploaderId);

    Page<FileEntity> findAllByOrderByUploadedAtDesc(Pageable pageable);

    Optional<FileEntity> findByStoredFileName(String storedFileName);

    @Query("SELECT f FROM FileEntity f WHERE f.originalFileName LIKE %:filename% " +
           "ORDER BY f.uploadedAt DESC")
    List<FileEntity> findByOriginalFileNameContaining(@Param("filename") String filename);

    @Query("SELECT f FROM FileEntity f WHERE f.mimeType LIKE :mimeType% " +
           "ORDER BY f.uploadedAt DESC")
    List<FileEntity> findByMimeTypeStartingWith(@Param("mimeType") String mimeType);

    @Query("SELECT COUNT(f) FROM FileEntity f WHERE f.uploader.id = :uploaderId")
    long countByUploaderId(@Param("uploaderId") Long uploaderId);

    @Query("SELECT SUM(f.fileSize) FROM FileEntity f WHERE f.uploader.id = :uploaderId")
    Long getTotalFileSizeByUploader(@Param("uploaderId") Long uploaderId);

    List<FileEntity> findTop10ByOrderByDownloadCountDesc();
} 