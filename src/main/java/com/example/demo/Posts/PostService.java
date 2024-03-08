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

    public Optional<Post> singlePost(ObjectId id){
        return postRepository.findById(id);
    }

    public List<PostToFrontendDTO> getLastTwentyPosts() {
        List<Post> posts = postRepository.findLastTwentyPosts();
        List<PostToFrontendDTO> postDTOs = new ArrayList<>();

        for (Post post : posts) {
            PostToFrontendDTO dto = new PostToFrontendDTO();
            BeanUtils.copyProperties(post, dto);
            dto.setId(post.getId().toString());
            dto.setUserId(post.getUserId().toString());
            User creator = userService.findById(post.getUserId()).orElse(null);
            if (creator != null) {
                dto.setCreatorName(creator.getName());
                dto.setCreatorImageUrl(creator.getImageUrl());
            }

            postDTOs.add(dto);
        }

        return postDTOs;
    }

}
