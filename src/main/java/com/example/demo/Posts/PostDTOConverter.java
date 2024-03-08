package com.example.demo.Posts;

import com.example.demo.User.User;
import com.example.demo.User.UserCreatingDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostDTOConverter {
    @Autowired
    private ModelMapper modelMapper;
    public PostDto convertPostToPostDto(Post post){
        PostDto postDto = modelMapper.map(post, PostDto.class);

        return postDto;
    }

    public Post convertPostDtoDTOToPost(PostDto postDto){
        Post post = modelMapper.map(postDto, Post.class);

        return post;
    }
}
