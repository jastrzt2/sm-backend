package com.example.demo.Posts;

import com.example.demo.User.User;
import com.example.demo.User.UserCreatingDTO;
import com.example.demo.User.UserService;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PostDTOConverter {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    @Lazy
    private UserService userService;
    public PostDto convertPostToPostDto(Post post){
        PostDto postDto = modelMapper.map(post, PostDto.class);

        return postDto;
    }

    public Post convertPostDtoDTOToPost(PostDto postDto){
        Post post = modelMapper.map(postDto, Post.class);

        return post;
    }


    public PostToFrontendDTO convertToFrontendPost(Post post) {
        PostToFrontendDTO dto = new PostToFrontendDTO();
        BeanUtils.copyProperties(post, dto);
        dto.setId(post.getId().toString());
        dto.setUserId(post.getUserId().toString());

        Set<String> likesStringSet = post.getLikes().stream()
                .map(ObjectId::toString)
                .collect(Collectors.toSet());
        dto.setLikes(likesStringSet);

        Set<String> commentsStringSet = post.getComments().stream()
                .map(ObjectId::toString)
                .collect(Collectors.toSet());
        dto.setComments(commentsStringSet);

        User creator = userService.findById(post.getUserId()).orElse(null);
        if (creator != null) {
            dto.setCreatorName(creator.getName());
            dto.setCreatorImageUrl(creator.getImageUrl());
        }

        return dto;
    }


}
