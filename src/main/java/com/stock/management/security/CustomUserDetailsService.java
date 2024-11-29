package com.stock.management.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.stock.management.data.jpa.model.UserInfo;
import com.stock.management.data.jpa.repository.UserSecurityRepository;
import com.stock.management.dto.UserDTO;
import com.stock.management.exception.BadRequestException;

import jakarta.validation.Valid;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserSecurityRepository userRepository;
    

    public CustomUserDetailsService(UserSecurityRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
    	LOGGER.info("Attempting to load user: {}", userId);

    	UserInfo user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
    	LOGGER.info("User found: {}", user.getUsername());
    	LOGGER.info("User found password: {}", user.getPassword());
    	return new org.springframework.security.core.userdetails.User(
    	        user.getUserId(), 
    	        user.getPassword(), // The encoded password should be returned here
    	        AuthorityUtils.createAuthorityList("ROLE_USER") // You can modify roles/authorities based on your application
    	    );
    }
    
    
}


