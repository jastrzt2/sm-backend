package com.example.demo.User;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDTOConverter {
    @Autowired
    private ModelMapper modelMapper;
    public UserCreatingDTO convertUserToUserDTO(User user){
        UserCreatingDTO userCreatingDTO = modelMapper.map(user, UserCreatingDTO.class);

        return userCreatingDTO;
    }

    public User convertUserDTOToUser(UserCreatingDTO userCreatingDTO){
        User user = modelMapper.map(userCreatingDTO, User.class);

        return user;
    }
}
