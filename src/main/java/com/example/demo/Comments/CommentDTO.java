package com.example.demo.Comments;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CommentDTO {
    private String id;
    private String text;
    private String userId;
    private String postId;
    private Date createdAt;
    private Date updatedAt;
    private List<String> likes = new ArrayList<>();
}
