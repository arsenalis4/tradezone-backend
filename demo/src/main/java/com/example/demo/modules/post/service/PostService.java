package com.example.demo.modules.post.service;

import com.example.demo.modules.post.dto.PostDto;
import com.example.demo.modules.post.entity.Post;
import com.example.demo.modules.post.repository.PostRepository;
import com.example.demo.modules.user.entity.User;
import com.example.demo.modules.user.repository.UserRepository;
import com.example.demo.modules.category.entity.Category;
import com.example.demo.modules.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public PostDto.Response createPost(PostDto.CreateRequest request, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + authorId));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + request.getCategoryId()));
        }

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .category(category)
                .isPublished(request.getIsPublished())
                .build();

        Post savedPost = postRepository.save(post);
        log.info("새 게시글 생성됨: {} by {}", savedPost.getTitle(), author.getEmail());

        return convertToResponse(savedPost);
    }

    @Transactional(readOnly = true)
    public PostDto.Response getPostById(Long id) {
        Post post = postRepository.findByIdAndIsPublishedTrue(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다: " + id));

        // 조회수 증가
        incrementViewCount(post);

        return convertToResponse(post);
    }

    @Transactional(readOnly = true)
    public Page<PostDto.Response> getAllPosts(PostDto.SearchRequest searchRequest) {
        Pageable pageable = createPageable(searchRequest);

        Page<Post> posts = postRepository.findPostsWithFilters(
                searchRequest.getKeyword(),
                searchRequest.getCategoryId(),
                searchRequest.getAuthorId(),
                searchRequest.getIsPublished(),
                pageable
        );

        return posts.map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostDto.Response> getPublishedPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByIsPublishedTrueOrderByCreatedAtDesc(pageable);
        return posts.map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostDto.Response> getPostsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByCategoryIdAndIsPublishedTrueOrderByCreatedAtDesc(categoryId, pageable);
        return posts.map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostDto.Response> getPostsByAuthor(Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByAuthorIdAndIsPublishedTrueOrderByCreatedAtDesc(authorId, pageable);
        return posts.map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public List<PostDto.Response> getPopularPosts() {
        return postRepository.findTop10ByIsPublishedTrueOrderByViewCountDesc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public PostDto.Response updatePost(Long id, PostDto.UpdateRequest request, Long authorId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다: " + id));

        // 작성자 권한 확인
        if (!post.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("게시글 수정 권한이 없습니다");
        }

        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + request.getCategoryId()));
            post.setCategory(category);
        }
        if (request.getIsPublished() != null) {
            post.setIsPublished(request.getIsPublished());
        }

        Post updatedPost = postRepository.save(post);
        log.info("게시글 업데이트됨: {}", updatedPost.getTitle());

        return convertToResponse(updatedPost);
    }

    public void deletePost(Long id, Long authorId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다: " + id));

        // 작성자 권한 확인
        if (!post.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("게시글 삭제 권한이 없습니다");
        }

        postRepository.delete(post);
        log.info("게시글 삭제됨: {}", post.getTitle());
    }

    public void likePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다: " + postId));

        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    @Transactional
    protected void incrementViewCount(Post post) {
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }

    private PostDto.Response convertToResponse(Post post) {
        PostDto.AuthorInfo authorInfo = PostDto.AuthorInfo.builder()
                .id(post.getAuthor().getId())
                .name(post.getAuthor().getName())
                .nickname(post.getAuthor().getNickname())
                .profileImageUrl(post.getAuthor().getProfileImageUrl())
                .build();

        PostDto.CategoryInfo categoryInfo = null;
        if (post.getCategory() != null) {
            categoryInfo = PostDto.CategoryInfo.builder()
                    .id(post.getCategory().getId())
                    .name(post.getCategory().getName())
                    .color(post.getCategory().getColor())
                    .build();
        }

        return PostDto.Response.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(authorInfo)
                .category(categoryInfo)
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .isPublished(post.getIsPublished())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private Pageable createPageable(PostDto.SearchRequest searchRequest) {
        Sort.Direction direction = searchRequest.getSortDirection().equalsIgnoreCase("ASC") 
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, searchRequest.getSortBy());
        return PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
    }
} 