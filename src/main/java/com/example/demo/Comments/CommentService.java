package com.example.demo.Comments;

import com.example.demo.Posts.Post;
import com.example.demo.Posts.PostService;
import com.example.demo.Posts.PostToFrontendDTO;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.demo.Session.AuthenticationService.isAllowed;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostService postService;

    public PostToFrontendDTO createComment(Map<String, String> body) {
        String postId = body.get("postId");
        String userId = body.get("userId");
        String content = body.get("text");

        Comment comment = new Comment();
        comment.setUserId(new ObjectId(userId));
        comment.setPostId(new ObjectId(postId));
        comment.setText(content);
        comment.setCreatedAt(Date.from(new Date().toInstant()));

        comment = commentRepository.save(comment);

        PostToFrontendDTO updatedPost = postService.addCommentToPost(comment.getPostId(), comment.getId());

        return updatedPost;
    }

    public List<CommentDTO> getCommentsForPost(String postId) {
        List<Comment> comments = commentRepository.findByPostId(new ObjectId(postId));

        return comments.stream().map(comment -> {
            CommentDTO dto = new CommentDTO();
            dto.setId(comment.getId().toString()); // Konwersja ObjectId na String
            dto.setText(comment.getText());
            dto.setUserId(comment.getUserId().toString()); // Konwersja ObjectId na String
            dto.setPostId(comment.getPostId().toString()); // Konwersja ObjectId na String
            dto.setCreatedAt(comment.getCreatedAt());
            dto.setUpdatedAt(comment.getUpdatedAt());
            dto.setLikes(comment.getLikes()); // Zakładam, że likes to lista ObjectId
            return dto;
        }).collect(Collectors.toList());
    }

    public Optional<PostToFrontendDTO> editComment(String commentId, Map<String, String> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Comment comment = commentRepository.findById(new ObjectId(commentId)).orElseThrow(() -> new RuntimeException("Comment not found"));
        if(!isAllowed(auth, comment.getUserId())) {
            throw new RuntimeException("You are not allowed to edit this comment");
        }
        comment.setText(body.get("text"));
        comment.setUpdatedAt(new Date());
        commentRepository.save(comment);

        return postService.getPost(comment.getPostId());
    }

    public Optional<PostToFrontendDTO> deleteComment(String commentId) {
        Comment comment = commentRepository.findById(new ObjectId(commentId)).orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.delete(comment);

        return postService.removeCommentFromPost(comment.getPostId(), comment.getId());
    }
}
