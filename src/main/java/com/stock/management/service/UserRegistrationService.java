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

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegistrationService.class);

    private final UserSecurityRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRegistrationService(UserSecurityRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with the provided details.
     * 
     * @param userDTO the data transfer object containing user details
     * @return the persisted UserInfo object
     * @throws BadRequestException if the user ID is invalid or already exists
     */
    public UserInfo registerUser(@Valid UserDTO userDTO) {
        validateUserId(userDTO.userId());
        checkIfUserExists(userDTO.userId());
        validatePassword(userDTO.password());

        UserInfo newUser = createUserEntity(userDTO);
        return userRepository.save(newUser);
    }

    /**
     * Validates if the provided user ID meets alphanumeric requirements.
     * 
     * @param userId the user ID to validate
     * @throws BadRequestException if the user ID is not alphanumeric
     */
    private void validateUserId(String userId) {
        if (userId == null || userId.isEmpty() || !userId.matches("[a-zA-Z0-9]+")) {
            LOGGER.debug("Invalid User ID: {}", userId);
            throw new BadRequestException("User ID must be alphanumeric.");
        }
    }

    /**
     * Checks if a user ID already exists in the repository.
     * 
     * @param userId the user ID to check
     * @throws BadRequestException if the user ID already exists
     */
    private void checkIfUserExists(String userId) {
        if (userRepository.existsById(userId)) {
            LOGGER.debug("User ID {} already exists.", userId);
            throw new BadRequestException("User ID already exists.");
        }
    }

    /**
     * Validates the password.
     * 
     * @param password the password to validate
     * @throws BadRequestException if the password is null or empty
     */
    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            LOGGER.debug("Password cannot be null or empty.");
            throw new BadRequestException("Password cannot be empty.");
        }
    }

    /**
     * Creates a new UserInfo entity from the provided UserDTO.
     * 
     * @param userDTO the data transfer object containing user details
     * @return a UserInfo entity populated with the provided details
     */
    private UserInfo createUserEntity(UserDTO userDTO) {
        UserInfo user = new UserInfo();
        user.setUserId(userDTO.userId());
        user.setEmail(userDTO.email());
        user.setUsername(userDTO.username());
        user.setPassword(passwordEncoder.encode(userDTO.password()));

        LOGGER.info("New user created: UserID={}, Username={}", userDTO.userId(), userDTO.username());
        return user;
    }
}
