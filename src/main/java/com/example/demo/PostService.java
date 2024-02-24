package com.example.demo;

import com.example.demo.User.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Post createPost(String postBody, String userId){
        Post post = postRepository.insert(new Post(postBody));

        mongoTemplate.update(User.class)
                .matching(Criteria.where("id").is(userId))
                .apply(new Update().push("posts").value(post))
                .first();

        return post;
    }

    public Post editPost(String  postId, String newPostBody) {
        // Find the post by its ID
        ObjectId objectId;
        try {
            objectId = new ObjectId(postId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid postId format: " + postId, e);
        }

        Optional<Post> postOptional = postRepository.findById(objectId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();

            // Update the post body
            post.setPostBody(newPostBody);

            // Save the updated post
            postRepository.save(post);

            return post;
        } else {
            // Handle the case where the post does not exist, e.g., throw an exception or return null
            throw new NoSuchElementException("Post with id " + postId + " not found.");
        }
    }


    public List<Post> allPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> singlePost(ObjectId id){
        return postRepository.findById(id);
    }
}
