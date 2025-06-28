package com.example.demo.modules.post.controller;

import com.example.demo.modules.post.dto.PostDto;
import com.example.demo.modules.post.service.PostService;
import com.example.demo.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Post", description = "게시글 관리 API")
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping
    public ResponseEntity<PostDto.Response> createPost(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody PostDto.CreateRequest request) {
        try {
            PostDto.Response response = postService.createPost(request, userPrincipal.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "게시글 조회", description = "특정 게시글을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<PostDto.Response> getPostById(@PathVariable Long id) {
        try {
            PostDto.Response response = postService.getPostById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "게시글 검색", description = "조건에 따라 게시글을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<Page<PostDto.Response>> searchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Boolean isPublished,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PostDto.SearchRequest searchRequest = PostDto.SearchRequest.builder()
                .keyword(keyword)
                .categoryId(categoryId)
                .authorId(authorId)
                .isPublished(isPublished)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .size(size)
                .build();

        Page<PostDto.Response> posts = postService.getAllPosts(searchRequest);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "공개 게시글 목록", description = "공개된 게시글 목록을 조회합니다.")
    @GetMapping("/public")
    public ResponseEntity<Page<PostDto.Response>> getPublicPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostDto.Response> posts = postService.getPublishedPosts(page, size);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "카테고리별 게시글", description = "특정 카테고리의 게시글을 조회합니다.")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<PostDto.Response>> getPostsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostDto.Response> posts = postService.getPostsByCategory(categoryId, page, size);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "작성자별 게시글", description = "특정 사용자의 게시글을 조회합니다.")
    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<PostDto.Response>> getPostsByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostDto.Response> posts = postService.getPostsByAuthor(authorId, page, size);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "인기 게시글", description = "조회수가 높은 게시글 상위 10개를 조회합니다.")
    @GetMapping("/popular")
    public ResponseEntity<List<PostDto.Response>> getPopularPosts() {
        List<PostDto.Response> posts = postService.getPopularPosts();
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<PostDto.Response> updatePost(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody PostDto.UpdateRequest request) {
        try {
            PostDto.Response response = postService.updatePost(id, request, userPrincipal.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            postService.deletePost(id, userPrincipal.getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "게시글 좋아요", description = "게시글에 좋아요를 추가합니다.")
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long id) {
        try {
            postService.likePost(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 