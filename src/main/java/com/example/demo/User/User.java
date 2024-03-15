package com.example.demo.User;

import com.example.demo.Posts.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String username;
    private String city;
    @Indexed(unique = true)
    private String email;
    private List<ObjectId> posts = new ArrayList<>();
    private List<ObjectId> savedPosts = new ArrayList<>();
    private List<ObjectId> likedPosts = new ArrayList<>();
    private List<ObjectId> comments = new ArrayList<>();
    @NotBlank(message = "Password is required")
    private String password;
    private String imageUrl;

}
