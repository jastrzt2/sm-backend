package com.example.demo.User;

import com.example.demo.util.ServiceResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.demo.util.JwtUtil.extractUsername;

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

    @GetMapping("/details")
    public ResponseEntity<Optional<UserCurrentUserDTO>> getUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        return new ResponseEntity<Optional<UserCurrentUserDTO>>(userService.getUserDetails(token), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ServiceResponse<UserCreatingDTO>> createUser(@RequestBody UserCreatingDTO userCreatingDTO) {
        ServiceResponse<UserCreatingDTO> response = userService.createUser(userCreatingDTO);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/save")
    public ResponseEntity<?> savePost(@RequestHeader("Authorization") String authorizationHeader,
                                      @RequestBody Map<String, String> body) {
        String token = authorizationHeader.substring(7); // Assuming Bearer token
        String postId = body.get("postId"); // Assuming the key is postId
        String userId = extractUsername(token);


        try {
            boolean success = userService.savePost(userId, postId);
            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
