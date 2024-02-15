package com.example.splitwise.services;

import com.example.splitwise.config.JWTConfig;
import com.example.splitwise.models.ErrorResponse;
import com.example.splitwise.models.user.SplitUser;
import com.example.splitwise.models.auth.LoginRequest;
import com.example.splitwise.models.auth.LoginResponse;
import com.example.splitwise.models.auth.SignUpRequest;
import com.example.splitwise.models.auth.SignUpResponse;
import com.example.splitwise.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager, JWTConfig jwtConfig, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public ResponseEntity<SignUpResponse> signUp(SignUpRequest signUpRequest) {
        //create the user here
        SplitUser user = SplitUser.builder().email(signUpRequest.getEmail()).name(signUpRequest.getName()).password(passwordEncoder.encode(signUpRequest.getPassword())).roles("ROLE_ADMIN,ROLE_USER").build();
        //now save this user
        userRepository.save(user);
        //now return the token
        String token = jwtConfig.createToken(user);

        SignUpResponse response = SignUpResponse.builder().token(token).email(user.getEmail()).build();

        return ResponseEntity.created(URI.create("user_created")).body(response);

    }

    public ResponseEntity login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            String email = authentication.getName();
            System.out.println(email);
            SplitUser user = SplitUser.builder().email(email).build();
            String token = jwtConfig.createToken(user);

            LoginResponse response = LoginResponse.builder().token(token).email(email).build();
            return ResponseEntity.ok().body(response);

        } catch (BadCredentialsException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid username or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new com.example.splitwise.models.ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

}
