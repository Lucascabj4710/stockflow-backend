package com.stockflow_backend.configuration;

import com.stockflow_backend.entities.UserEntity;
import com.stockflow_backend.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetail implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetail(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(()
                -> new UsernameNotFoundException("El username ingresa no corresponde a ningun usuario"));

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        userEntity.getRoleSet().forEach(role ->{
            authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.name())));
        });

        return new User(username,
                userEntity.getPassword(),
                userEntity.getEnabled(),
                userEntity.getAccountNonExpired(),
                userEntity.getCredentialsNonExpired(),
                userEntity.getAccountNonLocked(),
                authorities
                );
    }
}
