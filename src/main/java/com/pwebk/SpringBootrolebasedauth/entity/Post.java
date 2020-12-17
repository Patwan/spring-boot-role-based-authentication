package com.pwebk.SpringBootrolebasedauth.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "POSTS")
public class Post {

    private int postId;
    private String subject;
    private String description;
    private String userName;

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;
}
