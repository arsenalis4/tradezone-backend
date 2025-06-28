package com.example.demo.modules.file.controller;

import com.example.demo.modules.file.dto.FileDto;
import com.example.demo.modules.file.service.FileService;
import com.example.demo.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @Operation(summary = "파일 업로드", description = "새로운 파일을 업로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "파일 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping("/upload")
    public ResponseEntity<FileDto.UploadResponse> uploadFile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file) {
        try {
            FileDto.UploadResponse response = fileService.uploadFile(file, userPrincipal.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
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
} 