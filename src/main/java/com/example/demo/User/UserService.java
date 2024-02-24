package com.example.demo.User;

import com.mongodb.DuplicateKeyException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDTOConverter userDTOConverter;
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public Optional<User> singleUser(ObjectId id){
        return userRepository.findById(id);
    }

    @Transactional
    public User createUser(UserDTO userDTO) throws EmailAlreadyUsedException {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException("Email already in use: " + userDTO.getEmail());
        }

        Optional<User> existingUserByUsername = userRepository.findByUsername(userDTO.getUsername());
        if (existingUserByUsername.isPresent()) {
            throw new UsernameAlreadyUsedException("Username already in use: " + userDTO.getUsername());
        }
        // Convert UserDTO to User entity and save
        User user = userDTOConverter.convertUserDTOToUser(userDTO);
        return userRepository.save(user);
    }

}
