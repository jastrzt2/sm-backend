package com.example.demo.Posts;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Date;

@Data
public class PostDto {
    private ObjectId id;
    private String caption;
    private String imageUrl;
    private String location;
    private ObjectId userId;
    private Date createdAt;
    private Date updatedAt;
}
