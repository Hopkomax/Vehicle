package ua.service.vehicles.services;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
public class MyUserDetailsService implements UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.equals("admin")) {
            return new User("admin", "{bcrypt}$2a$10$W7XxgERpkaXcZGpNS5Mo4.zBpy1RpkGIfhzsuIUrkgmHGZhesxrs6",
                    Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN")));
        } else if (username.equals("user")) {
            return new User("user", "{bcrypt}$2y$12$J1NRxm3Q.FibWa/VCN15j.qEw9fbccnxF2auG5duBAq/ZXPINChOW",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        }
      throw new UsernameNotFoundException("Username not found");
    }
}
