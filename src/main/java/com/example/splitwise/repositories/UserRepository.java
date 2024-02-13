package com.example.splitwise.repositories;

import com.example.splitwise.models.SplitUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<SplitUser, Integer> {

    Optional<SplitUser> findByEmail(String email);
}
