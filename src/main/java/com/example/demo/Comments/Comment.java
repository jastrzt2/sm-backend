package com.example.demo.Comments;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "comments")
@Data
public class Comment {
    @Id
    private ObjectId id;
    private String text;
    private ObjectId userId;
    private ObjectId postId;
    private Date createdAt;
    private Date updatedAt;
    private List<ObjectId> likes = new ArrayList<>();
}