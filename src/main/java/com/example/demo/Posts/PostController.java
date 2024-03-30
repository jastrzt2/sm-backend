package com.example.demo.Posts;

import com.example.demo.Comments.Comment;
import com.example.demo.Comments.CommentDTO;
import com.example.demo.Comments.CommentRepository;
import com.example.demo.Comments.CommentService;
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
    @Autowired
    private CommentService commentService;

    @GetMapping("/get/{id}")
    public ResponseEntity<Optional<PostToFrontendDTO>> getPostById(@PathVariable ObjectId id) {
        return new ResponseEntity<>(postService.singlePost(id), HttpStatus.OK);
    }

    @GetMapping("/get")
    public List<PostToFrontendDTO> getPosts(@RequestParam(defaultValue = "0") int page) {
        int size = 9;
        return postService.findPaginated(page, size);
    }

    @PostMapping("/search")
    public List<PostToFrontendDTO> searchPosts(@RequestBody Map<String, String> body) {
        String searchTerm = body.get("searchTerm");
        return postService.searchPosts(searchTerm);
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

        Post savedPost = postService.createPost(postDto);
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

    @PostMapping("/edit")
    public ResponseEntity<?> editPost(@RequestParam("postId") String postId,
                                      @RequestParam("caption") String caption,
                                      @RequestParam(value = "location", required = false) String location,
                                      @RequestParam(value = "tags", required = false) String tags,
                                      @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            PostToFrontendDTO updatedPost = postService.editPost(postId, caption, location, tags, file);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> editPost(@RequestParam("postId") String postId) {
        try {
            PostToFrontendDTO updatedPost = postService.deletePost(postId);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getComments/{postId}")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable String postId) {
        try {
            List<CommentDTO> comments = commentService.getCommentsForPost(postId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
