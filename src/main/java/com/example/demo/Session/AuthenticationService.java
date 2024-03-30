package com.example.demo.Session;

import com.example.demo.User.User;
import com.example.demo.User.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository; // Assuming you have a UserRepository

    public User authenticate(String email, String password) throws AuthenticationException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (passwordMatches(password, user.getPassword())) {
            return user;
        } else {
            throw new BadCredentialsException("Invalid email/password supplied");
        }
    }

    public static boolean isAllowed(Authentication authentication, ObjectId ownerId){
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        User user = (User) authentication.getPrincipal();
        ObjectId userId = new ObjectId(user.getId());
        if ( !userId.equals(ownerId)){
            return false;
        }
        return true;
    }

    private boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

