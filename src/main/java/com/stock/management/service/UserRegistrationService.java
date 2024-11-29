package com.stock.management.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.stock.management.data.jpa.model.UserInfo;
import com.stock.management.data.jpa.repository.UserSecurityRepository;
import com.stock.management.dto.UserDTO;
import com.stock.management.exception.BadRequestException;

import jakarta.validation.Valid;

@Service
public class UserRegistrationService {

    private final UserSecurityRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegistrationService.class);
    
    @Autowired
    public UserRegistrationService(UserSecurityRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserInfo registerUser(@Valid UserDTO userDTO) {
        validateUserId(userDTO.getUserId());

        if (userRepository.existsById(userDTO.getUserId())) {
        	LOGGER.debug("User ID {} already exists.", userDTO.getUserId());
            throw new BadRequestException("User ID already exists.");
        }

        UserInfo user = new UserInfo();
        user.setUserId(userDTO.getUserId());
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userRepository.save(user);
    }
    

    private void validateUserId(String userId) {
        if (!userId.matches("[a-zA-Z0-9]+")) {
            throw new BadRequestException("User ID must be alphanumeric.");
        }
    }
}

