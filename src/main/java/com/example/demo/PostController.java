package com.example.demo;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Map<String, String> payload){
        return new ResponseEntity<Post>(postService.createPost(payload.get("postBody"), payload.get("userId")), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return new ResponseEntity<List<Post>>(postService.allPosts(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Post>> getPostById(@PathVariable ObjectId id) {
        return new ResponseEntity<Optional<Post>>(postService.singlePost(id), HttpStatus.OK);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Post> editPost(@PathVariable String postId, @RequestBody PostEditRequest request) {
        try {
            Post updatedPost = postService.editPost(postId, request.getPostBody());
            return ResponseEntity.ok(updatedPost);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
