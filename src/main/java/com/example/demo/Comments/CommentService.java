package com.example.demo.Comments;

import com.example.demo.Posts.Post;
import com.example.demo.Posts.PostDTOConverter;
import com.example.demo.Posts.PostService;
import com.example.demo.Posts.PostToFrontendDTO;
import com.example.demo.User.User;
import com.example.demo.User.UserService;
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
    @Autowired
    private UserService userService;
    @Autowired
    private CommentConverter commentConverter;

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
        userService.addCommentToUser(comment.getUserId(), comment.getId());

        PostToFrontendDTO updatedPost = postService.addCommentToPost(comment.getPostId(), comment.getId());

        return updatedPost;
    }

    public List<CommentDTO> getCommentsForPost(String postId) {
        List<Comment> comments = commentRepository.findByPostId(new ObjectId(postId));

        return comments.stream().map(comment -> commentConverter.commentToDTO(comment)).collect(Collectors.toList());
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
        userService.removeCommentFromUser(comment.getUserId(), comment.getId());

        return postService.removeCommentFromPost(comment.getPostId(), comment.getId());
    }

    public CommentDTO likeComment(String id) {
        Comment comment = commentRepository.findById(new ObjectId(id)).orElseThrow(() -> new RuntimeException("Comment not found"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        if (comment.getLikes().contains(new ObjectId(user.getId()))) {
            comment.getLikes().remove(new ObjectId(user.getId()));
            userService.removeLikedCommentFromUser(user.getId(), comment.getId());
        } else {
            comment.getLikes().add(new ObjectId(user.getId()));
            userService.addLikedCommentToUser(user.getId(), comment.getId());
        }

        CommentDTO dto = commentConverter.commentToDTO(commentRepository.save(comment));

        return dto;
    }
}
