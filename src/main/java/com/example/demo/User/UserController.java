package com.example.demo.User;

import com.example.demo.Posts.PostToFrontendDTO;
import com.example.demo.util.ServiceResponse;
import org.apache.tomcat.util.http.parser.Authorization;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.demo.Session.JwtUtil.extractUsername;

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
    public ResponseEntity<Optional<UserCurrentUserDTO>> getUserById(@PathVariable ObjectId id) {
        return new ResponseEntity<Optional<UserCurrentUserDTO>>(userService.singleUser(id).map(user -> userDTOConverter.convertUserToUserCurrentDTO(user)), HttpStatus.OK);
    }

    @GetMapping("/details")
    public ResponseEntity<Optional<UserCurrentUserDTO>> getUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }
        Optional<UserCurrentUserDTO> userDto = userService.getUserDetails(token);
        return new ResponseEntity<Optional<UserCurrentUserDTO>>(userDto, HttpStatus.OK);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ServiceResponse<UserCreatingDTO>> createUser(@RequestBody UserCreatingDTO userCreatingDTO) {
        ServiceResponse<UserCreatingDTO> response = userService.createUser(userCreatingDTO);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/save")
    public ResponseEntity<?> savePost(@RequestHeader("Authorization") String authorizationHeader,
                                      @RequestBody Map<String, String> body) {
        try {
            boolean success = userService.savePost(body.get("postId"));
            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> editUser( @PathVariable String id,
                                       @RequestParam(required = false) MultipartFile file,
                                       @RequestParam("name") String name,
                                       @RequestParam("bio") String bio,
                                       @RequestParam("city") String city) {
        try {
            return ResponseEntity.ok(userService.updateUser(name, bio, city, file, id));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getSavedPosts")
    public ResponseEntity<List<PostToFrontendDTO>> getSavedPosts() {
        return new ResponseEntity<List<PostToFrontendDTO>>(userService.getSavedPosts(), HttpStatus.OK);
    }

}
