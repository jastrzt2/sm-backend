package com.example.demo.Posts;

import com.example.demo.Images.ImageService;
import com.example.demo.User.User;
import com.example.demo.User.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    @Autowired
    private ImageService imageService;

    public Post savePost(PostCreatedDto postDto) {
        String imageUrl = "";
        if(postDto.getFile() != null) {
            try {
                imageUrl = (String) imageService.uploadImage(postDto.getFile()).get("url");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Post post = new Post();
        BeanUtils.copyProperties(postDto, post);
        post.setId(new ObjectId());
        post.setUserId(new ObjectId(postDto.getUserId()));
        post.setCreatedAt(Date.from(new Date().toInstant()));

        post.setImageUrl(imageUrl);

        post = postRepository.save(post);

        userService.addPostIdToUser(new ObjectId(postDto.getUserId()), post.getId());

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
