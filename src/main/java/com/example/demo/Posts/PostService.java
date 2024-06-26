package com.example.demo.Posts;

import com.example.demo.Images.ImageService;
import com.example.demo.User.User;
import com.example.demo.User.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostDTOConverter postDTOConverter;
    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    private ImageService imageService;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PostService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Post createPost(PostCreatedDto postDto) {
        String imageUrl = "";
        if(postDto.getFile() != null) {
            try {
                imageUrl = (String) imageService.uploadImage(postDto.getFile()).get("url");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Post post = new Post();
        BeanUtils.copyProperties(postDto, post);
        post.setId(new ObjectId());
        post.setUserId(new ObjectId(postDto.getUserId()));
        post.setCreatedAt(Date.from(new Date().toInstant()));

        post.setImageUrl(imageUrl);

        post = postRepository.save(post);

        userService.addPostIdToUser(new ObjectId(postDto.getUserId()), post.getId());

        return post;
    }


    public List<Post> allPosts() {
        return postRepository.findAll();
    }

    public Optional<PostToFrontendDTO> singlePost(ObjectId id) {
        return postRepository.findById(id)
                .map(postDTOConverter::convertToFrontendPost);
    }

    private List<Post> getWatchedUserPosts(User currentUser, int limit) {
        List<ObjectId> watchedUserIds = currentUser.getWatched();
        return mongoTemplate.find(Query.query(Criteria.where("userId").in(watchedUserIds)).with(Sort.by(Sort.Direction.DESC, "createdAt")).limit(limit), Post.class);
    }

    private List<Post> getRecentPosts(int limit) {
        return mongoTemplate.find(Query.query(new Criteria()).with(Sort.by(Sort.Direction.DESC, "createdAt")).limit(limit), Post.class);
    }

    private List<Post> getPopularPosts(int limit) {
        Date oneWeekAgo = Date.from(Instant.now().minus(1, ChronoUnit.WEEKS));

        List<Post> lastHundredPosts = mongoTemplate.find(
                Query.query(Criteria.where("createdAt").gte(oneWeekAgo))
                        .with(Sort.by(Sort.Direction.DESC, "createdAt"))
                        .limit(100),
                Post.class
        );

        return lastHundredPosts.stream()
                .sorted((post1, post2) -> Integer.compare(post2.getLikes().size(), post1.getLikes().size()))
                .limit(limit)
                .collect(Collectors.toList());
    }



    public List<PostToFrontendDTO> getTwentyPosts() {
        List<Post> posts = postRepository.findLastTwentyPosts();
        List<PostToFrontendDTO> postDTOs = new ArrayList<>();

        for (Post post : posts) {
            PostToFrontendDTO dto = postDTOConverter.convertToFrontendPost(post);
            postDTOs.add(dto);
        }

        return postDTOs;
    }

    public PostToFrontendDTO likePost(String postId, String userIdString) {
        ObjectId postIdObj = new ObjectId(postId);
        ObjectId userIdObj = new ObjectId(userIdString);

        Post post = postRepository.findById(postIdObj).orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userService.findById(userIdObj).orElseThrow(() -> new RuntimeException("User not found"));

        boolean isLiked = post.getLikes().contains(userIdObj);
        if (isLiked) {
            post.getLikes().remove(userIdObj);
            user.getLikedPosts().remove(postIdObj);
        } else {
            post.getLikes().add(userIdObj);
            user.getLikedPosts().add(postIdObj);
        }

        postRepository.save(post);
        userService.save(user);

        return postDTOConverter.convertToFrontendPost(post);
    }

    public PostToFrontendDTO editPost(String postId, String caption, String location, MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        User user = (User) authentication.getPrincipal();
        Post currentPost = postRepository.findById(new ObjectId(postId)).orElseThrow(() -> new RuntimeException("Post not found"));
        if(!currentPost.getUserId().toString().equals(user.getId().toString())) {
            throw new RuntimeException("User not authorized to edit this post");
        }

        Post post = new Post();
        BeanUtils.copyProperties(currentPost, post);

        post.setId(new ObjectId(postId));
        post.setUserId(currentPost.getUserId());
        post.setCaption(caption);
        post.setLocation(location);
        if (file != null && !file.isEmpty()) {
            try {
                imageService.deleteImage(currentPost.getImageUrl());
                String imageUrlString = (String) imageService.uploadImage(file).get("url");
                post.setImageUrl(imageUrlString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            post.setImageUrl(currentPost.getImageUrl());
        }
        post.setUpdatedAt(Date.from(new Date().toInstant()));
        postRepository.save(post);

        return postDTOConverter.convertToFrontendPost(post);
    }

    public PostToFrontendDTO deletePost(String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        User user = (User) authentication.getPrincipal();
        Post post = postRepository.findById(new ObjectId(postId)).orElseThrow(() -> new RuntimeException("Post not found"));
        if(!post.getUserId().toString().equals(user.getId().toString())) {
            throw new RuntimeException("User not authorized to delete this post");
        }

        postRepository.deleteById(new ObjectId(postId));
        imageService.deleteImage(post.getImageUrl());
        userService.removePostIdFromUser(user.getId(), new ObjectId(postId));

        return postDTOConverter.convertToFrontendPost(post);
    }

    public PostToFrontendDTO addCommentToPost(ObjectId postId, ObjectId id) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        post.getComments().add(id);
        postRepository.save(post);

        return postDTOConverter.convertToFrontendPost(post);
    }

    public Optional<PostToFrontendDTO> getPost(ObjectId postId) {
        return postRepository.findById(postId)
                .map(post -> postDTOConverter.convertToFrontendPost(post));
    }

    public Optional<PostToFrontendDTO> removeCommentFromPost(ObjectId postId, ObjectId commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        boolean isRemoved = post.getComments().removeIf(cid -> cid.equals(commentId));

        if (isRemoved) {
            postRepository.save(post);
        }
        return Optional.ofNullable(postDTOConverter.convertToFrontendPost(post));
    }

    public List<PostToFrontendDTO> findByCursor(String cursor, int size) {
        Query query = new Query();
        if (cursor != null && !cursor.isEmpty()) {
            ObjectId cursorId = new ObjectId(cursor);
            query.addCriteria(Criteria.where("_id").lt(cursorId));
        }

        query.with(Sort.by(Sort.Direction.DESC, "_id")).limit(size);

        List<Post> posts = mongoTemplate.find(query, Post.class);
        return posts.stream()
                .map(postDTOConverter::convertToFrontendPost)
                .collect(Collectors.toList());
    }

    public List<PostToFrontendDTO> findPostsOfWatchedUsersByCursor(String cursor, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        User user = (User) authentication.getPrincipal();

        if (user.getWatched() == null || user.getWatched().isEmpty()) {
            return Collections.emptyList();
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").in(user.getWatched()));

        if (cursor != null && !cursor.isEmpty()) {
            ObjectId cursorId = new ObjectId(cursor);
            query.addCriteria(Criteria.where("_id").lt(cursorId));
        }

        query.with(Sort.by(Sort.Direction.DESC, "_id")).limit(size);

        List<Post> posts = mongoTemplate.find(query, Post.class);
        return posts.stream()
                .map(postDTOConverter::convertToFrontendPost)
                .collect(Collectors.toList());
    }



    public List<PostToFrontendDTO> searchPosts(String searchTerm) {
        List<Post> posts = postRepository.findByCaptionContainingIgnoreCase(searchTerm);
        List<PostToFrontendDTO> postDTOs = new ArrayList<>();

        for (Post post : posts) {
            PostToFrontendDTO dto = postDTOConverter.convertToFrontendPost(post);
            postDTOs.add(dto);
        }

        return postDTOs;
    }

    public List<PostToFrontendDTO> getSavedPosts(User user) {
        List<ObjectId> savedPostIds = user.getSavedPosts();
        List<PostToFrontendDTO> postDTOs = new ArrayList<>();

        List<Post> savedPosts = postRepository.findByIdIn(savedPostIds);

        for (Post post : savedPosts) {
            PostToFrontendDTO dto = postDTOConverter.convertToFrontendPost(post);
            postDTOs.add(dto);
        }

        return postDTOs;
    }

    public List<PostToFrontendDTO> getPostsByIds(List<ObjectId> objectIdList) {
        List<Post> posts = postRepository.findByIdIn(objectIdList);
        List<PostToFrontendDTO> postDTOs = new ArrayList<>();

        for (Post post : posts) {
            PostToFrontendDTO dto = postDTOConverter.convertToFrontendPost(post);
            postDTOs.add(dto);
        }

        return postDTOs;
    }
}
