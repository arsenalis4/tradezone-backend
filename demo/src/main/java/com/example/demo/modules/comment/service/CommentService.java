package com.example.demo.modules.comment.service;

import com.example.demo.modules.comment.dto.CommentDto;
import com.example.demo.modules.comment.entity.Comment;
import com.example.demo.modules.comment.repository.CommentRepository;
import com.example.demo.modules.user.entity.User;
import com.example.demo.modules.user.repository.UserRepository;
import com.example.demo.modules.post.entity.Post;
import com.example.demo.modules.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentDto.Response createComment(CommentDto.CreateRequest request, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + authorId));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다: " + request.getPostId()));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findByIdAndIsDeletedFalse(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("부모 댓글을 찾을 수 없습니다: " + request.getParentId()));
        }

        Comment comment = Comment.builder()
                .content(request.getContent())
                .author(author)
                .post(post)
                .parent(parent)
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("새 댓글 생성됨: {} by {} on post {}", 
                savedComment.getId(), author.getEmail(), post.getTitle());

        return convertToResponse(savedComment);
    }

    @Transactional(readOnly = true)
    public CommentDto.Response getCommentById(Long id) {
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다: " + id));

        return convertToResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto.Response> getCommentsByPostId(Long postId) {
        List<Comment> parentComments = commentRepository
                .findByPostIdAndParentIsNullAndIsDeletedFalseOrderByCreatedAtAsc(postId);

        return parentComments.stream()
                .map(this::convertToResponseWithReplies)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentDto.Response> getCommentsByAuthor(Long authorId) {
        return commentRepository.findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(authorId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public CommentDto.Response updateComment(Long id, CommentDto.UpdateRequest request, Long authorId) {
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다: " + id));

        // 작성자 권한 확인
        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("댓글 수정 권한이 없습니다");
        }

        comment.setContent(request.getContent());
        Comment updatedComment = commentRepository.save(comment);
        
        log.info("댓글 업데이트됨: {}", updatedComment.getId());
        return convertToResponse(updatedComment);
    }

    public void deleteComment(Long id, Long authorId) {
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다: " + id));

        // 작성자 권한 확인
        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("댓글 삭제 권한이 없습니다");
        }

        comment.setIsDeleted(true);
        comment.setContent("삭제된 댓글입니다.");
        commentRepository.save(comment);

        log.info("댓글 삭제됨: {}", comment.getId());
    }

    public void likeComment(Long commentId) {
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다: " + commentId));

        comment.setLikeCount(comment.getLikeCount() + 1);
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public long getCommentCountByPost(Long postId) {
        return commentRepository.countByPostIdAndIsDeletedFalse(postId);
    }

    @Transactional(readOnly = true)
    public long getCommentCountByAuthor(Long authorId) {
        return commentRepository.countByAuthorIdAndIsDeletedFalse(authorId);
    }

    private CommentDto.Response convertToResponse(Comment comment) {
        CommentDto.AuthorInfo authorInfo = CommentDto.AuthorInfo.builder()
                .id(comment.getAuthor().getId())
                .name(comment.getAuthor().getName())
                .nickname(comment.getAuthor().getNickname())
                .profileImageUrl(comment.getAuthor().getProfileImageUrl())
                .build();

        CommentDto.PostInfo postInfo = CommentDto.PostInfo.builder()
                .id(comment.getPost().getId())
                .title(comment.getPost().getTitle())
                .build();

        return CommentDto.Response.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(authorInfo)
                .post(postInfo)
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .likeCount(comment.getLikeCount())
                .isDeleted(comment.getIsDeleted())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    private CommentDto.Response convertToResponseWithReplies(Comment comment) {
        CommentDto.Response response = convertToResponse(comment);

        // 대댓글 조회
        List<Comment> replies = commentRepository
                .findByParentIdAndIsDeletedFalseOrderByCreatedAtAsc(comment.getId());
        
        List<CommentDto.Response> replyResponses = replies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        response.setReplies(replyResponses);
        return response;
    }
} 