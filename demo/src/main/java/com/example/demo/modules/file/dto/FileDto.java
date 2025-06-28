package com.example.demo.modules.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class FileDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadRequest {
        private String description;
        private Boolean isPublic = false;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String originalFileName;
        private String storedFileName;
        private String filePath;
        private String downloadUrl;
        private Long fileSize;
        private String mimeType;
        private UploaderInfo uploader;
        private Integer downloadCount;
        private LocalDateTime uploadedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploaderInfo {
        private Long id;
        private String name;
        private String nickname;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DownloadRequest {
        private Long fileId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadResponse {
        private String message;
        private Response file;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private Long id;
        private String originalFileName;
        private Long fileSize;
        private String mimeType;
        private String uploaderName;
        private Integer downloadCount;
        private LocalDateTime uploadedAt;
    }
} 