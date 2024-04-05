package com.example.demo.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;
@Data
public class UserCurrentUserDTO {
        private String id;
        private String name;
        private String username;
        private String city;
        private String bio;
        private String email;
        private List<String> posts = new ArrayList<>();
        private List<String> savedPosts = new ArrayList<>();
        private List<String> comments = new ArrayList<>();
        private String imageUrl;

}
