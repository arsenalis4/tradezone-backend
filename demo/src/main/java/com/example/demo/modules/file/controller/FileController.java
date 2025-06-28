package com.example.demo.modules.file.controller;

import com.example.demo.modules.file.dto.FileDto;
import com.example.demo.modules.file.service.FileService;
import com.example.demo.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File", description = "파일 관리 API")
public class FileController {

    private final FileService fileService;

    @Operation(
        summary = "파일 업로드", 
        description = "새로운 파일을 업로드합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                schema = @Schema(implementation = FileUploadRequestSchema.class))
        )
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "파일 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDto.UploadResponse> uploadFile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "업로드할 파일", required = true, 
                      content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "파일 설명")
            @RequestParam(value = "description", required = false) String description) {
        try {
            FileDto.UploadResponse response = fileService.uploadFile(file, userPrincipal.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "파일 조회", description = "특정 파일 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<FileDto.Response> getFileById(@PathVariable Long id) {
        try {
            FileDto.Response response = fileService.getFileById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Swagger용 스키마 클래스
    @Schema(name = "FileUploadRequestSchema", description = "파일 업로드 요청 스키마")
    public static class FileUploadRequestSchema {
        @Schema(description = "업로드할 파일", type = "string", format = "binary", required = true)
        public MultipartFile file;
        
        @Schema(description = "파일 설명", example = "프로필 이미지")
        public String description;
    }
} 