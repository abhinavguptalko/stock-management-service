package com.stock.management.junit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.stock.management.controller.UserManagementController;
import com.stock.management.data.jpa.model.UserInfo;
import com.stock.management.dto.UserDTO;

@ExtendWith(MockitoExtension.class)
class UserManagementControllerTest {

	/*
	 * @Mock private UserService userService;
	 * 
	 * @InjectMocks private UserManagementController userManagementController;
	 * 
	 * @Test void testAddUser() { UserDTO userDTO = new UserDTO();
	 * userDTO.setUserId("user123"); userDTO.setUsername("JohnDoe");
	 * userDTO.setEmail("johndoe@example.com");
	 * 
	 * UserInfo expectedUser = new UserInfo("user123", "JohnDoe",
	 * "johndoe@example.com");
	 * 
	 * when(userService.addUser(userDTO)).thenReturn(expectedUser);
	 * 
	 * ResponseEntity<UserInfo> response =
	 * userManagementController.addUser(userDTO);
	 * 
	 * assertNotNull(response); assertEquals(expectedUser, response.getBody());
	 * verify(userService, times(1)).addUser(userDTO); }
	 */
}
