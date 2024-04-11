package com.example.demo.Comments;

import com.example.demo.User.User;
import com.example.demo.User.UserCreatingDTO;
import com.example.demo.User.UserCurrentUserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentConverter {
    @Autowired
    private ModelMapper modelMapper;
    public CommentDTO commentToDTO(Comment comment){
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);

        return commentDTO;
    }

    public Comment DTOToComment(CommentDTO commentDTO){
        Comment comment = modelMapper.map(commentDTO, Comment.class);

        return comment;
    }
}