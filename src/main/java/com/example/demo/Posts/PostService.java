package com.example.demo.Posts;

import com.example.demo.Images.ImageService;
import com.example.demo.User.User;
import com.example.demo.User.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostDTOConverter postDTOConverter;
    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;

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

    public List<PostToFrontendDTO> getLastTwentyPosts() {
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

    public PostToFrontendDTO editPost(String postId, String caption, String location, String tags, MultipartFile file) {
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
        post.setTags(tags);
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

    public List<PostToFrontendDTO> findPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<Post> posts = postRepository.findAll(pageable).getContent();
        List<PostToFrontendDTO> postDTOs = new ArrayList<>();

        for (Post post : posts) {
            PostToFrontendDTO dto = postDTOConverter.convertToFrontendPost(post);
            postDTOs.add(dto);
        }

        return postDTOs;
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
}
