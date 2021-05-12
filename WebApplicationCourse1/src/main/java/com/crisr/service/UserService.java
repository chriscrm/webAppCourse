package com.crisr.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.crisr.dto.UserDTO;

public interface UserService extends UserDetailsService {
	
	UserDTO createUser(UserDTO user);
	UserDTO getUser(String email);
	UserDTO getUserByUserId(String userId);
	UserDTO updateUser(String id, UserDTO userDTO);

}
