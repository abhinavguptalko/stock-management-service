package com.stock.management.junit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.stock.management.controller.UserManagementController;
import com.stock.management.data.jpa.model.UserInfo;
import com.stock.management.dto.UserDTO;
import com.stock.management.service.UserRegistrationService;

class UserManagementControllerTest {

    @Mock
    private UserRegistrationService userRegistrationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserManagementController userManagementController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddUser_Success() {
        // Arrange
        UserDTO userDTO = new UserDTO("testUser", "Test User", "test@example.com", "password123");
        UserInfo createdUser = new UserInfo();
        createdUser.setUserId(userDTO.userId());
        createdUser.setUsername(userDTO.username());
        createdUser.setEmail(userDTO.email());

        when(userRegistrationService.registerUser(userDTO)).thenReturn(createdUser);

        // Act
        ResponseEntity<UserInfo> response = userManagementController.addUser(userDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdUser, response.getBody());
        verify(userRegistrationService, times(1)).registerUser(userDTO);
    }

    @Test
    void testLogin_Success() {
        // Arrange
        UserDTO userDTO = new UserDTO("testUser", "Test User", "test@example.com", "password123");
        Authentication authentication = mock(Authentication.class); // Mock the Authentication object
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.userId(), userDTO.password())))
                .thenReturn(authentication); // Simulate successful authentication

        // Act
        ResponseEntity<Void> response = userManagementController.login(userDTO);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.userId(), userDTO.password()));
    }


    @Test
    void testLogin_Failure() {
        // Arrange
        UserDTO userDTO = new UserDTO("testUser", "Test User", "test@example.com", "wrongPassword");
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDTO.userId(), userDTO.password());

        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager).authenticate(authToken);

        // Act & Assert
        Exception exception = assertThrows(BadCredentialsException.class, () -> userManagementController.login(userDTO));
        assertEquals("Invalid credentials", exception.getMessage());
        verify(authenticationManager, times(1))
                .authenticate(new UsernamePasswordAuthenticationToken(userDTO.userId(), userDTO.password()));
    }
}
