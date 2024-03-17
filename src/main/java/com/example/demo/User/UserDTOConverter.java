package com.example.demo.User;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;

import java.util.List;

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

    public UserCurrentUserDTO convertUserToUserCurrentDTO(User user){
        UserCurrentUserDTO userCurrentUserDTO = modelMapper.map(user, UserCurrentUserDTO.class);
        return userCurrentUserDTO;
    }

    public User convertUserCurrentDTOtoUser(UserCurrentUserDTO userCurrentUserDTO){
        User user = modelMapper.map(userCurrentUserDTO, User.class);

        return user;
    }
}
