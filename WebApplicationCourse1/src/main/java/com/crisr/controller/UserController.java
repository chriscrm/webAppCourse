package com.crisr.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crisr.dto.UserDTO;
import com.crisr.exceptions.UserServiceException;
import com.crisr.model.request.UserDetailModel;
import com.crisr.model.response.ErrorMessages;
import com.crisr.model.response.UserRest;
import com.crisr.service.UserService;

@RestController
@RequestMapping("users")
public class UserController {
	
	@Autowired
	UserService userService;

	@GetMapping(path = "/{id}")
	public UserRest getUser(@PathVariable String id) {
		
		UserRest returnValue = new UserRest();
		
		UserDTO userDTO = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDTO, returnValue);
		
		return returnValue;
	}

	@PostMapping
	public UserRest createUser(@RequestBody UserDetailModel userDetail) throws Exception {
		
		UserRest returnValue = new UserRest();
		
		if (userDetail.getFirstName().isEmpty() 
				|| userDetail.getEmail().isEmpty() 
				|| userDetail.getLastName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		
		UserDTO userDTO = new UserDTO();
		BeanUtils.copyProperties(userDetail, userDTO);
		
		UserDTO createdUser = userService.createUser(userDTO);
		BeanUtils.copyProperties(createdUser, returnValue);
		
		
		return returnValue;
	}

	@PutMapping
	public String updateUser() {
		return "update user was called";
	}

	@DeleteMapping
	public String deleteUser() {
		return "delete user was called";
	}

}
