package com.example.demo.Posts;

import com.example.demo.User.User;
import com.example.demo.User.UserService;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
    private Set<String> likes = new HashSet<>();
    private Set<String> comments = new HashSet<>();

}