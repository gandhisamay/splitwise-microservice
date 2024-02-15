package com.example.splitwise.controllers;


import com.example.splitwise.models.SplitUser;
import com.example.splitwise.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping(value = "/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<SplitUser>> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping(value = "/new")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<String> createUser(@RequestBody SplitUser user) {
        return userService.createUser(user);
    }

    @PostMapping(value = "/new/many")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> createManyUsers(@RequestBody List<SplitUser> users) {
        return userService.createManyUsers(users);
    }

    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        //now delete it using the repository;
        return userService.deleteUser(id);
    }

}
