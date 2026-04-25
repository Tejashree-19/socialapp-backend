package com.socialapp.dto;

public record CommentResponse(
        Long id,
        Long postId,
        Long authorId,
        String content,
        int depthLevel,
        String createdAt
) {}