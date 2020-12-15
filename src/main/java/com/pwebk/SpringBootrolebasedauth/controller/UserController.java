package com.pwebk.SpringBootrolebasedauth.controller;

import com.pwebk.SpringBootrolebasedauth.entity.User;
import com.pwebk.SpringBootrolebasedauth.repository.UserRepository;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final String DEFAULT_USER = "ROLE_USER";

    @Autowired
    private UserRepository repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/join")
    public String joinGroup(@RequestBody User user){
        user.setRoles(DEFAULT_USER);
        String encryptedPwd = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPwd);
        repository.save(user);
        return "Hi" + user.getUserName() + " ,welcome to group";
    }

}
