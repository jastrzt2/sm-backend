package com.example.demo.User;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Error
class UsernameAlreadyUsedException extends RuntimeException {
    public UsernameAlreadyUsedException(String email) {
        super("Username already used: " + email);
    }
}
