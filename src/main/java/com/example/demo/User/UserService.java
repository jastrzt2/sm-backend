package com.example.demo.User;

import com.example.demo.util.ServiceResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.demo.Session.JwtUtil.extractUsername;

@Service
public class UserService {
    private final MongoTemplate mongoTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDTOConverter userDTOConverter;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public Optional<User> singleUser(ObjectId id) {
        return userRepository.findById(id);
    }

    @Transactional
    public ServiceResponse<UserCreatingDTO> createUser(UserCreatingDTO userCreatingDTO) {
        ServiceResponse<UserCreatingDTO> response = new ServiceResponse<>();
        if (userRepository.findByEmail(userCreatingDTO.getEmail()).isPresent() && userRepository.findByUsername(userCreatingDTO.getUsername()).isPresent()) {
            response.setSuccess(false);
            response.setMessage("User with this email and username already exists");
            return response;
        }
        if (userRepository.findByEmail(userCreatingDTO.getEmail()).isPresent()) {
            return new ServiceResponse<>(null, false, "User with this email already exists");
        }
        if (userRepository.findByUsername(userCreatingDTO.getUsername()).isPresent()) {
            return new ServiceResponse<>(null, false, "User with this username already exists");
        }
        User user = userDTOConverter.convertUserDTOToUser(userCreatingDTO);
        String hashedPassword = passwordEncoder.encode(userCreatingDTO.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
        user.setPassword(null);
        return new ServiceResponse<>(userDTOConverter.convertUserToUserDTO(user), true, "User created successfully");
    }


    public Optional<UserCurrentUserDTO> getUserDetails(String token) {
        if( token.startsWith("\"") || token.endsWith("\"") ) //needed because send String starts and ends with quotation marks
            token = token.replaceAll("^\"|\"$", "");
        String username = extractUsername(token);
        if(username == null) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username)
                .map(user -> userDTOConverter.convertUserToUserCurrentDTO(user));
    }

    public void addPostIdToUser(ObjectId userId, ObjectId postId) {
        Query query = new Query(Criteria.where("_id").is(userId));
        Update update = new Update().push("posts", postId);
        mongoTemplate.updateFirst(query, update, User.class);
    }

    public Optional<User> findById(ObjectId id) {
        return userRepository.findById(id);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public boolean savePost( String postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            User user = (User) authentication.getPrincipal();
            ObjectId postIdObj = new ObjectId(postId);
            boolean isSaved = user.getSavedPosts().contains(new ObjectId(postId));
            if (isSaved) {
                user.getSavedPosts().remove(postIdObj);
            } else {
                user.getSavedPosts().add(postIdObj);
            }

            save(user);
            return true;
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public void removePostIdFromUser(String id, ObjectId objectId) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        Update update = new Update().pull("posts", objectId);
        mongoTemplate.updateFirst(query, update, User.class);
    }
}
