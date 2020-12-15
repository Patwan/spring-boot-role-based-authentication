package com.pwebk.SpringBootrolebasedauth.repository;

import com.pwebk.SpringBootrolebasedauth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Id;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Id> {
    Optional<User> findByUserName(String userName);
}
