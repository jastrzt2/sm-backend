package com.example.demo.Comments;

import com.example.demo.Posts.PostService;
import com.example.demo.Posts.PostToFrontendDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<?> createComment(@RequestBody Map<String, String> body) {
        try {
            PostToFrontendDTO updatedPost = commentService.createComment(body);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editComment(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            Optional<PostToFrontendDTO> updatedPost = commentService.editComment(id, body);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable String id) {
        try {
            Optional<PostToFrontendDTO> updatedPost = commentService.deleteComment(id);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/like/{id}")
    public ResponseEntity<?> likeComment(@PathVariable String id) {
        try {
            CommentDTO updatedComment = commentService.likeComment(id);
            return ResponseEntity.ok(updatedComment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
