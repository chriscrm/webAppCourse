package com.crisr.controller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	//@GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@GetMapping(path = "/{id}")
	public UserRest getUser(@PathVariable String id) {
		
		UserRest returnValue = new UserRest();
		
		UserDTO userDTO = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDTO, returnValue);
		
		return returnValue;
	}

	//@PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@PostMapping
	public UserRest createUser(@RequestBody UserDetailModel userDetail) throws Exception {
		
		UserRest returnValue = new UserRest();
		
		if (userDetail.getFirstName().isEmpty() 
				|| userDetail.getEmail().isEmpty() 
				|| userDetail.getLastName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		
		//for embedded object into an object use modelmapper dependency
		//UserDTO userDTO = new UserDTO();
		//BeanUtils.copyProperties(userDetail, userDTO);
		
		//ModelMapper dependency for map/copy properties
		ModelMapper modelMapper = new ModelMapper();
		UserDTO userDTO = modelMapper.map(userDetail, UserDTO.class);
		
		UserDTO createdUser = userService.createUser(userDTO);
		//BeanUtils.copyProperties(createdUser, returnValue);
		returnValue = modelMapper.map(createdUser, UserRest.class);
		
		return returnValue;
	}

	@PutMapping(path = "/{id}")
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailModel userDetail) {
		
		UserRest returnValue = new UserRest();
		
		UserDTO userDTO = new UserDTO();
		BeanUtils.copyProperties(userDetail, userDTO);
		
		UserDTO updateUser = userService.updateUser(id, userDTO);
		BeanUtils.copyProperties(updateUser, returnValue);
		
		
		return returnValue;
	}

	@DeleteMapping(path = "/{id}")
	public String deleteUser(@PathVariable String userId) {
		
		userService.deleteUser(userId);
		
		return "The user with id " + userId + " was deleted successful";
	}
	
	//request query string params
	@GetMapping
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "25") int limit){
		
		List<UserRest> returnValue = new ArrayList<>();
		
		List<UserDTO> users = userService.getUsers(page, limit);
		
		for (UserDTO userDTO : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDTO, userModel);
			returnValue.add(userModel);
		}
		
		return returnValue;
	}
	

}
