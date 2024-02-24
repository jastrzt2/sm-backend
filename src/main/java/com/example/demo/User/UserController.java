package com.example.demo.User;

import com.example.demo.util.ValidationErrorResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserDTOConverter userDTOConverter;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<List<User>>(userService.allUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable ObjectId id) {
        return new ResponseEntity<Optional<User>>(userService.singleUser(id), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUserDto = userDTOConverter.convertUserToUserDTO(userService.createUser(userDTO));
            return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
        } catch (EmailAlreadyUsedException | UsernameAlreadyUsedException e) {
            ValidationErrorResponse errorResponse = new ValidationErrorResponse();
            if (e instanceof EmailAlreadyUsedException) {
                errorResponse.addFieldError("email", e.getMessage());
            } else if (e instanceof UsernameAlreadyUsedException) {
                errorResponse.addFieldError("username", e.getMessage());
            }
            System.out.println("Error: " + errorResponse.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
