package com.pwebk.SpringBootrolebasedauth.service;

import com.pwebk.SpringBootrolebasedauth.entity.User;
import com.pwebk.SpringBootrolebasedauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GroupUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> user = repository.findByUserName(username);

        return user.map(GroupUserDetails::new)
                .orElseThrow(() ->new UsernameNotFoundException(username + "doesn't exist in the system"));
    }
}
