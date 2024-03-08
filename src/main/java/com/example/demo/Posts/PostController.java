package com.example.demo.Posts;

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

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Post>> getPostById(@PathVariable ObjectId id) {
        return new ResponseEntity<Optional<Post>>(postService.singlePost(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostDto postDto) {
        Post savedPost = postService.savePost(postDto);
        return ResponseEntity.ok(savedPost);
    }

    @GetMapping("/getPosts")
    public ResponseEntity<List<PostToFrontendDTO>> getLastTwentyPosts() {
        System.out.println("Getting last 20 posts");
        List<PostToFrontendDTO> posts = postService.getLastTwentyPosts();
        return ResponseEntity.ok(posts);
    }
}
