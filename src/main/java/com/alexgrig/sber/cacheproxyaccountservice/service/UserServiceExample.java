package com.alexgrig.sber.cacheproxyaccountservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public class UserServiceExample {
    @Autowired
    UserService userService;

    @GetMapping("users")
    public ResponseEntity<List<User>> getAll() {

        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @GetMapping("users/{id}")
    public ResponseEntity<User> getById(@PathVariable long id) {

        Optional<User> user = userService.getById(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            throw new RecordNotFoundException();
        }
    }
}
