package com.example.splitwise.repositories;

import com.example.splitwise.models.SplitUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<SplitUser, Integer> {

}
