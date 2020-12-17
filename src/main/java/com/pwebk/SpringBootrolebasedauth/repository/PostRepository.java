package com.pwebk.SpringBootrolebasedauth.repository;

import com.pwebk.SpringBootrolebasedauth.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {

}
