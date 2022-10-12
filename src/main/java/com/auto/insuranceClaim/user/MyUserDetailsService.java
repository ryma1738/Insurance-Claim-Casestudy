package com.auto.insuranceClaim.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userRes = userRepo.findByEmail(email);
        if(!userRes.isPresent())
            throw new UsernameNotFoundException("Could not findUser with email = " + email);
        User user = userRes.get();
        if (user.getRole().equals("admin"))
            return new org.springframework.security.core.userdetails.User(email, user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        else if (user.getRole().equals("employee"))
            return new org.springframework.security.core.userdetails.User(email, user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
        else return new org.springframework.security.core.userdetails.User(email, user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}