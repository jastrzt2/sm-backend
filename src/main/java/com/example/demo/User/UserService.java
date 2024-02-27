package com.example.demo.User;

import com.example.demo.util.ServiceResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public Optional<User> singleUser(ObjectId id) {
        return userRepository.findById(id);
    }

    @Transactional
    public ServiceResponse<UserDTO> createUser(UserDTO userDTO) {
        ServiceResponse<UserDTO> response = new ServiceResponse<>();
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent() && userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            response.setSuccess(false);
            response.setMessage("User with this email and username already exists");
            return response;
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            return new ServiceResponse<>(null, false, "User with this email already exists");
        }
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            return new ServiceResponse<>(null, false, "User with this username already exists");
        }
        User user = userDTOConverter.convertUserDTOToUser(userDTO);
        String hashedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
        user.setPassword(null);
        return new ServiceResponse<>(userDTOConverter.convertUserToUserDTO(user), true, "User created successfully");
    }


}
