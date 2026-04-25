package com.socialapp.controller;

import com.socialapp.entity.Post;
import com.socialapp.repository.PostRepository;
import com.socialapp.service.ViralityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final ViralityService viralityService;

    // ✅ Create Post
    @PostMapping
    public Post createPost(@RequestBody Post post) {
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    // ✅ Like Post
    @PostMapping("/{postId}/like")
    public String likePost(@PathVariable Long postId,
                           @RequestParam Long userId) {

        if (userId > 1000) {
            return "Bots cannot like posts";
        }

        viralityService.updateScore(postId, "LIKE");
        return "Post liked successfully";
    }

    // ✅ GET VIRALITY SCORE (THIS WAS MISSING / NOT ACTIVE)
    @GetMapping("/{postId}/score")
    public String getScore(@PathVariable Long postId) {

        Long score = viralityService.getScore(postId);

        return "Virality Score: " + (score != null ? score : 0);
    }
}