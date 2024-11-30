package com.stock.management.junit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.stock.management.data.jpa.model.UserInfo;
import com.stock.management.data.jpa.repository.UserSecurityRepository;
import com.stock.management.dto.UserDTO;
import com.stock.management.exception.BadRequestException;
import com.stock.management.service.UserRegistrationService;

public class UserRegistrationServiceTest {

    @Mock
    private UserSecurityRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRegistrationService userRegistrationService;

    private UserDTO validUserDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validUserDTO = new UserDTO("user123", "user@example.com", "username", "password123");
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        UserInfo newUser = new UserInfo();
        newUser.setUserId(validUserDTO.userId());
        newUser.setEmail(validUserDTO.email());
        newUser.setUsername(validUserDTO.username());
        newUser.setPassword("encodedPassword");

        // Mock repository and password encoding
        when(userRepository.existsById(validUserDTO.userId())).thenReturn(false);
        when(passwordEncoder.encode(validUserDTO.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserInfo.class))).thenReturn(newUser);

        // Act
        UserInfo result = userRegistrationService.registerUser(validUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals(validUserDTO.userId(), result.getUserId());
        assertEquals(validUserDTO.username(), result.getUsername());
        assertEquals(validUserDTO.email(), result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        verify(userRepository, times(1)).save(any(UserInfo.class));
    }

    @Test
    void testRegisterUser_InvalidUserId_ShouldThrowBadRequestException() {
        // Arrange
        UserDTO invalidUserDTO = new UserDTO("user@123", "user@example.com", "username", "password123");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userRegistrationService.registerUser(invalidUserDTO);
        });

        assertEquals("User ID must be alphanumeric.", exception.getMessage());
    }

    @Test
    void testRegisterUser_UserAlreadyExists_ShouldThrowBadRequestException() {
        // Arrange
        when(userRepository.existsById(validUserDTO.userId())).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userRegistrationService.registerUser(validUserDTO);
        });

        assertEquals("User ID already exists.", exception.getMessage());
    }

    @Test
    void testPasswordEncryption_ShouldEncryptPasswordDuringRegistration() {
        // Arrange
        when(userRepository.existsById(validUserDTO.userId())).thenReturn(false);
        when(passwordEncoder.encode(validUserDTO.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserInfo.class))).thenReturn(new UserInfo());

        // Act
        userRegistrationService.registerUser(validUserDTO);

        // Assert
        verify(passwordEncoder, times(1)).encode(validUserDTO.password());
    }

    @Test
    void testRegisterUser_EmptyUserId_ShouldThrowBadRequestException() {
        // Arrange
        UserDTO invalidUserDTO = new UserDTO("", "user@example.com", "username", "password123");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userRegistrationService.registerUser(invalidUserDTO);
        });

        assertEquals("User ID must be alphanumeric.", exception.getMessage());
    }

    @Test
    void testRegisterUser_NullUserId_ShouldThrowBadRequestException() {
        // Arrange
        UserDTO invalidUserDTO = new UserDTO(null, "user@example.com", "username", "password123");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userRegistrationService.registerUser(invalidUserDTO);
        });

        assertEquals("User ID must be alphanumeric.", exception.getMessage());
    }

    @Test
    void testRegisterUser_EmptyPassword_ShouldThrowBadRequestException() {
        // Arrange
        UserDTO invalidUserDTO = new UserDTO("user123", "user@example.com", "username", "");

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userRegistrationService.registerUser(invalidUserDTO);
        });

        assertEquals("Password cannot be empty.", exception.getMessage());
    }

    @Test
    void testRegisterUser_NullPassword_ShouldThrowBadRequestException() {
        // Arrange
        UserDTO invalidUserDTO = new UserDTO("user123", "user@example.com", "username", null);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userRegistrationService.registerUser(invalidUserDTO);
        });

        assertEquals("Password cannot be empty.", exception.getMessage());
    }
}
