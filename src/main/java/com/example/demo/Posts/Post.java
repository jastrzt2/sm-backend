package com.example.demo.Posts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Document(collection = "posts")
@Data
public class Post {
    @Id
    private ObjectId id;
    private String caption;
    private String location;
    private String tags;
    private String imageUrl;
    private ObjectId userId;
    private Date createdAt;
    private Date updatedAt;
    private List<ObjectId> likes;
    private List<ObjectId> comments;

}
