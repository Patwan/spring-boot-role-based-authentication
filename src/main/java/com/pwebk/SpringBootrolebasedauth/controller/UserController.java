package com.pwebk.SpringBootrolebasedauth.controller;

import com.pwebk.SpringBootrolebasedauth.entity.User;
import com.pwebk.SpringBootrolebasedauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Id;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final String DEFAULT_USER = "ROLE_USER";
    private static final String[] ADMIN_ACCESS = {"ROLE_ADMIN" , "ROLE_MODERATOR"};
    private static final String[] MODERATOR_ACCESS = {"ROLE_MODERATOR"};

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

    //If Logged in user is ADMIN -> ADMIN or MODERATOR
    //If loggedin is Moderator -> MODERATOR
    @GetMapping("/access/{userId}/{userRole}")
    public String giveAccessToUser(@PathVariable Id userId, @PathVariable String userRole, Principal principal){
        User user = repository.findById(userId).get();
        List<String> activeRoles = getRolesByLoggedInUser(principal);

        String newRole ="";
        if(activeRoles.contains(userRole)){
            newRole = user.getRoles() + "," + userRole;
            user.setRoles(newRole);
        }
        repository.save(user);
        return "Hi " + user.getUserName() + " New role assigned to you by " + principal.getName();
    }

    private List<String> getRolesByLoggedInUser(Principal principal){
        String roles = getLoggedInUser(principal).getRoles();
        List<String> assignRoles = Arrays.stream(roles.split(",")).collect(Collectors.toList());
        if(assignRoles.contains("ROLE_ADMIN")){
            return Arrays.stream(ADMIN_ACCESS).collect((Collectors.toList()));
        }
        if(assignRoles.contains("ROLE_MODERATOR")){
            return Arrays.stream(MODERATOR_ACCESS).collect((Collectors.toList()));
        }
        return Collections.emptyList();
    }

    private User getLoggedInUser(Principal principal){
        return repository.findByUserName(principal.getName()).get();
    }

}
