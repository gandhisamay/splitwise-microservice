package com.example.splitwise.services;

import com.example.splitwise.exceptions.UserNotFoundException;
import com.example.splitwise.models.SplitUser;
import com.example.splitwise.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<List<SplitUser>> getAllUsers() {
        List<SplitUser> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    public ResponseEntity<String> createUser(SplitUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new ResponseEntity<String>("success", HttpStatus.CREATED);
    }

    public ResponseEntity<String> createManyUsers(List<SplitUser> users) {
        users.parallelStream().forEach(user -> user.setPassword(passwordEncoder.encode(user.getPassword())));
        userRepository.saveAll(users);
        return new ResponseEntity<String>("success", HttpStatus.CREATED);
    }

    public ResponseEntity<String> deleteUser(int id) throws UserNotFoundException {

        boolean userExists = userRepository.existsById(id);

        if (userExists) {
            userRepository.deleteById(id);
            return new ResponseEntity<String>("success", HttpStatus.OK);
        }

        throw new UserNotFoundException(id);
    }
}
