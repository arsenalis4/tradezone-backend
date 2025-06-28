package com.example.demo.modules.file.service;

import com.example.demo.modules.file.dto.FileDto;
import com.example.demo.modules.file.entity.FileEntity;
import com.example.demo.modules.file.repository.FileRepository;
import com.example.demo.modules.user.entity.User;
import com.example.demo.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public FileDto.UploadResponse uploadFile(MultipartFile file, Long uploaderId) {
        if (file.isEmpty()) {
            throw new RuntimeException("업로드할 파일이 없습니다");
        }

        User uploader = userRepository.findById(uploaderId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + uploaderId));

        try {
            String originalFileName = file.getOriginalFilename();
            String storedFileName = generateStoredFileName(originalFileName);
            String s3Key = "uploads/" + storedFileName;
            
            // S3에 파일 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String s3Url = String.format("https://%s.s3.%s.amazonaws.com/%s", 
                    bucketName, "ap-northeast-2", s3Key);

            FileEntity fileEntity = FileEntity.builder()
                    .originalFileName(originalFileName)
                    .storedFileName(storedFileName)
                    .filePath(s3Url)
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType())
                    .uploader(uploader)
                    .build();

            FileEntity savedFile = fileRepository.save(fileEntity);
            log.info("파일 업로드 완료: {} by {}", originalFileName, uploader.getEmail());

            FileDto.Response fileResponse = convertToResponse(savedFile);
            return FileDto.UploadResponse.builder()
                    .message("파일 업로드가 완료되었습니다")
                    .file(fileResponse)
                    .build();

        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public FileDto.Response getFileById(Long id) {
        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다: " + id));
        return convertToResponse(file);
    }

    public void deleteFile(Long fileId, Long uploaderId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다: " + fileId));

        // 업로더 권한 확인
        if (!file.getUploader().getId().equals(uploaderId)) {
            throw new RuntimeException("파일 삭제 권한이 없습니다");
        }

        // S3에서 파일 삭제
        try {
            String s3Key = "uploads/" + file.getStoredFileName();
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            
            // DB에서 파일 정보 삭제
            fileRepository.delete(file);
            log.info("파일 삭제됨: {}", file.getOriginalFileName());
            
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", e.getMessage());
            throw new RuntimeException("파일 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    private String generateStoredFileName(String originalFileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String extension = getFileExtension(originalFileName);
        return timestamp + "_" + uuid + extension;
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.lastIndexOf(".") > 0) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }

    private FileDto.Response convertToResponse(FileEntity file) {
        FileDto.UploaderInfo uploaderInfo = FileDto.UploaderInfo.builder()
                .id(file.getUploader().getId())
                .name(file.getUploader().getName())
                .nickname(file.getUploader().getNickname())
                .build();

        return FileDto.Response.builder()
                .id(file.getId())
                .originalFileName(file.getOriginalFileName())
                .storedFileName(file.getStoredFileName())
                .filePath(file.getFilePath())
                .downloadUrl(file.getFilePath())
                .fileSize(file.getFileSize())
                .mimeType(file.getMimeType())
                .uploader(uploaderInfo)
                .downloadCount(file.getDownloadCount())
                .uploadedAt(file.getUploadedAt())
                .build();
    }
} 