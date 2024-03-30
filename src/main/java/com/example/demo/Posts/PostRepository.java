package com.example.demo.Posts;

import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, ObjectId> {
    default List<Post> findLastTwentyPosts() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        return findAll(pageable).getContent();
    }

    List<Post> findByCaptionContainingIgnoreCase(String searchTerm);
}
