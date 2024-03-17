package com.example.demo.Posts;

import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<Optional<PostToFrontendDTO>> getPostById(@PathVariable ObjectId id) {
        return new ResponseEntity<Optional<PostToFrontendDTO>>(postService.singlePost(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestParam("userId") String userId,
            @RequestParam("caption") String caption,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        PostCreatedDto postDto = new PostCreatedDto();
        postDto.setUserId(String.valueOf(new ObjectId(userId)));
        postDto.setCaption(caption);
        if (location != null) {
            postDto.setLocation(location);
        }
        if (tags != null) {
            postDto.setTags(tags);
        }
        if (file != null && !file.isEmpty()) {
            postDto.setFile(file);
        }

        Post savedPost = postService.savePost(postDto);
        return ResponseEntity.ok(savedPost);
    }

    @GetMapping("/getPosts")
    public ResponseEntity<List<PostToFrontendDTO>> getLastTwentyPosts() {
        System.out.println("Getting last 20 posts");
        List<PostToFrontendDTO> posts = postService.getLastTwentyPosts();
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable String postId, @RequestBody Map<String, String> body) {
        try {
            String userId = body.get("userId");
            PostToFrontendDTO updatedPost = postService.likePost(postId, userId);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
