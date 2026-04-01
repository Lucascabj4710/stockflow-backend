package com.stockflow_backend.services;

import com.stockflow_backend.dto.request.UserEntityRequestDTO;
import com.stockflow_backend.entities.AuthLoginRequestDTO;
import com.stockflow_backend.entities.Role;
import com.stockflow_backend.entities.UserEntity;
import com.stockflow_backend.exceptions.UserAlreadyExistsException;
import com.stockflow_backend.repositories.UserRepository;
import com.stockflow_backend.utils.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserDetailsService userDetailsService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(UserEntityRequestDTO userEntityRequestDTO){

        if(userRepository.existsByUsername(userEntityRequestDTO.getUsername())){
            throw new UserAlreadyExistsException("User already exists");
        }

        UserEntity userEntity = new UserEntity();
        Set<Role> authorities = new HashSet<>();
        authorities.add(Role.USER);

        userEntity.setEnabled(true);
        userEntity.setAccountNonExpired(true);
        userEntity.setAccountNonLocked(true);
        userEntity.setCredentialsNonExpired(true);
        userEntity.setUsername(userEntityRequestDTO.getUsername());
        userEntity.setPassword(passwordEncoder.encode(userEntityRequestDTO.getPassword()));
        userEntity.setRoleSet(authorities);

        userRepository.save(userEntity);
    }

    public String login(AuthLoginRequestDTO authLoginRequestDTO){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authLoginRequestDTO.username(),
                        authLoginRequestDTO.password())
        );

        return jwtUtils.createToken(authentication);
    }

}
