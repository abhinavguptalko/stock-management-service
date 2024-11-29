package com.stock.management.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.management.data.jpa.model.UserInfo;
import com.stock.management.dto.UserDTO;
import com.stock.management.service.UserRegistrationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserManagementController {

	private final UserRegistrationService userRegistrationService;
	
    private AuthenticationManager authenticationManager;
	
	 private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementController.class);

    @Autowired
    public UserManagementController(UserRegistrationService userRegistrationService, AuthenticationManager authenticationManager) {
        this.userRegistrationService = userRegistrationService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(("/addUser"))
    public ResponseEntity<UserInfo> addUser(@Valid @RequestBody UserDTO userDTO) {
        UserInfo createdUser = userRegistrationService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    @PostMapping(("/login"))
    public ResponseEntity<String> login(@Valid @RequestBody UserDTO userDTO) {
    	LOGGER.debug("User Login for  {}", userDTO.getUserId());
             // Authenticate the user
             authenticationManager.authenticate(
                 new UsernamePasswordAuthenticationToken(userDTO.getUserId(), userDTO.getPassword()));
             return ResponseEntity.ok("Login successful");
         
    }
	    
}
