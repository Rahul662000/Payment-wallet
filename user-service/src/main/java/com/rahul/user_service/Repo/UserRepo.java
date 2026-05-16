package com.rahul.user_service.Repo;

import com.rahul.user_service.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User , Long> {
   Optional<User> findByEmail(String email);
}
