package com.example.demo.Posts;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostCreatedDto {
    private String userId;
    private String caption;
    private String location;
    private MultipartFile file;
}
