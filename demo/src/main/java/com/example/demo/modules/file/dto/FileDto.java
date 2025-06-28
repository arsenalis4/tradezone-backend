package com.example.demo.modules.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(name = "FileUploadRequest", description = "파일 업로드 요청 DTO")
    public static class UploadRequest {
        private String description;
        
        @Builder.Default
        private Boolean isPublic = false;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "FileResponse", description = "파일 정보 응답 DTO")
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
    @Schema(name = "FileUploaderInfo", description = "파일 업로더 정보 DTO")
    public static class UploaderInfo {
        private Long id;
        private String name;
        private String nickname;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "FileDownloadRequest", description = "파일 다운로드 요청 DTO")
    public static class DownloadRequest {
        private Long fileId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "FileUploadResponse", description = "파일 업로드 응답 DTO")
    public static class UploadResponse {
        private String message;
        private Response file;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "FileListResponse", description = "파일 목록 응답 DTO")
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