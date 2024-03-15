package com.example.demo.Posts;

import com.example.demo.User.User;
import com.example.demo.User.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostDTOConverter postDTOConverter;
    @Autowired
    private UserService userService;

    public Post savePost(PostDto postDto) {
        Post post = postDTOConverter.convertPostDtoDTOToPost(postDto);
        post.setId(new ObjectId());
        post.setCreatedAt(Date.from(new Date().toInstant()));

        post = postRepository.save(post);

        userService.addPostIdToUser(postDto.getUserId(), post.getId());

        return post;
    }


    public List<Post> allPosts() {
        return postRepository.findAll();
    }

    public Optional<PostToFrontendDTO> singlePost(ObjectId id) {
        return postRepository.findById(id)
                .map(postDTOConverter::convertToFrontendPost);
    }

    public List<PostToFrontendDTO> getLastTwentyPosts() {
        List<Post> posts = postRepository.findLastTwentyPosts();
        List<PostToFrontendDTO> postDTOs = new ArrayList<>();

        for (Post post : posts) {
            PostToFrontendDTO dto = postDTOConverter.convertToFrontendPost(post);

            postDTOs.add(dto);
        }

        return postDTOs;
    }

    public PostToFrontendDTO likePost(String postId, String userIdString) {
        ObjectId postIdObj = new ObjectId(postId);
        ObjectId userIdObj = new ObjectId(userIdString);

        Post post = postRepository.findById(postIdObj).orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userService.findById(userIdObj).orElseThrow(() -> new RuntimeException("User not found"));

        boolean isLiked = post.getLikes().contains(userIdObj);
        if (isLiked) {
            post.getLikes().remove(userIdObj);
            user.getLikedPosts().remove(postIdObj);
        } else {
            post.getLikes().add(userIdObj);
            user.getLikedPosts().add(postIdObj);
        }

        postRepository.save(post);
        userService.save(user);

        return postDTOConverter.convertToFrontendPost(post);
    }
}
