package com.example.demo.Posts;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Date;

@Data
public class PostToFrontendDTO {
    private String id;
    private String caption;
    private String location;
    private String tags;
    private String imageUrl;
    private String userId;
    private String creatorName;
    private String creatorImageUrl;
    private Date createdAt;
    private Date updatedAt;

}
