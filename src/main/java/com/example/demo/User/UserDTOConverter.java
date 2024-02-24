package com.example.demo.User;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDTOConverter {
    @Autowired
    private ModelMapper modelMapper;
    public UserDTO convertUserToUserDTO(User user){
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return userDTO;
    }

    public User convertUserDTOToUser(UserDTO userDTO){
        User user = modelMapper.map(userDTO, User.class);

        return user;
    }
}
