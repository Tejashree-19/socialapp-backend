package com.socialapp.controller;
import com.socialapp.dto.*;
import com.socialapp.dto.ErrorResponse;
import com.socialapp.entity.Comment;
import com.socialapp.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Add comment to a post")
    @ApiResponses(value = {

        @ApiResponse(
            responseCode = "200",
            description = "Comment added successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CommentResponse.class)
            )
        ),

        @ApiResponse(
            responseCode = "429",
            description = "Too Many Requests",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PostMapping("/{postId}/comments")
    public CommentResponse addComment(
            @PathVariable Long postId,
            @RequestBody Comment comment) {

        Comment saved = commentService.addComment(postId, comment);

        return new CommentResponse(
                saved.getId(),
                saved.getPostId(),
                saved.getAuthorId(),
                saved.getContent(),
                saved.getDepthLevel(),
                saved.getCreatedAt().toString()
        );
    }
}