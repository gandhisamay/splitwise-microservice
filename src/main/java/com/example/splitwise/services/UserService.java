package com.example.splitwise.services;

import com.example.splitwise.exceptions.UserNotFoundException;
import com.example.splitwise.models.SplitUser;
import com.example.splitwise.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<List<SplitUser>> getAllUsers(){
        List<SplitUser> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    public ResponseEntity<String> createUser(SplitUser user) {
        userRepository.save(user);
        return new ResponseEntity<String>("success", HttpStatus.CREATED);
    }

    public ResponseEntity<String> createManyUsers(List<SplitUser> users){
        userRepository.saveAll(users);
        return new ResponseEntity<String>("success", HttpStatus.CREATED);
    }

    public ResponseEntity<String> deleteUser(int id) throws UserNotFoundException{

        boolean userExists = userRepository.existsById(id);

        if(userExists){
            userRepository.deleteById(id);
            return new ResponseEntity<String>("success", HttpStatus.OK);
        }

        throw new UserNotFoundException(id);
    }
}
