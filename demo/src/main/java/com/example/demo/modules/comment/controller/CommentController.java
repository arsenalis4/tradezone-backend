package com.example.demo.modules.comment.controller;

import com.example.demo.modules.comment.dto.CommentDto;
import com.example.demo.modules.comment.service.CommentService;
import com.example.demo.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Comment", description = "댓글 관리 API")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 생성", description = "새로운 댓글을 생성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping
    public ResponseEntity<CommentDto.Response> createComment(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CommentDto.CreateRequest request) {
        try {
            CommentDto.Response response = commentService.createComment(request, userPrincipal.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "댓글 조회", description = "특정 댓글을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<CommentDto.Response> getCommentById(@PathVariable Long id) {
        try {
            CommentDto.Response response = commentService.getCommentById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "게시글의 댓글 목록", description = "특정 게시글의 모든 댓글을 조회합니다.")
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto.Response>> getCommentsByPostId(@PathVariable Long postId) {
        List<CommentDto.Response> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "사용자의 댓글 목록", description = "특정 사용자의 모든 댓글을 조회합니다.")
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<CommentDto.Response>> getCommentsByAuthor(@PathVariable Long authorId) {
        List<CommentDto.Response> comments = commentService.getCommentsByAuthor(authorId);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "내 댓글 목록", description = "현재 로그인한 사용자의 모든 댓글을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my")
    public ResponseEntity<List<CommentDto.Response>> getMyComments(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<CommentDto.Response> comments = commentService.getCommentsByAuthor(userPrincipal.getId());
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<CommentDto.Response> updateComment(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CommentDto.UpdateRequest request) {
        try {
            CommentDto.Response response = commentService.updateComment(id, request, userPrincipal.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            commentService.deleteComment(id, userPrincipal.getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "댓글 좋아요", description = "댓글에 좋아요를 추가합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeComment(@PathVariable Long id) {
        try {
            commentService.likeComment(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "게시글 댓글 수", description = "특정 게시글의 댓글 수를 조회합니다.")
    @GetMapping("/count/post/{postId}")
    public ResponseEntity<Long> getCommentCountByPost(@PathVariable Long postId) {
        long count = commentService.getCommentCountByPost(postId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "사용자 댓글 수", description = "특정 사용자의 댓글 수를 조회합니다.")
    @GetMapping("/count/author/{authorId}")
    public ResponseEntity<Long> getCommentCountByAuthor(@PathVariable Long authorId) {
        long count = commentService.getCommentCountByAuthor(authorId);
        return ResponseEntity.ok(count);
    }
} 