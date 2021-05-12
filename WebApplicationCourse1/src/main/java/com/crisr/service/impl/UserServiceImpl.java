package com.crisr.service.impl;

import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.crisr.CustomUtils;
import com.crisr.UserRepository;
import com.crisr.dto.UserDTO;
import com.crisr.entity.UserEntity;
import com.crisr.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	CustomUtils customUtils;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDTO createUser(UserDTO user) {

		if (userRepository.findByEmail(user.getEmail()) != null)
			throw new RuntimeException("User Email already exists");

		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);

		// hardcoded temp
		String publicUserId = customUtils.generateUserId(10);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		UserDTO returnValue = new UserDTO();

		UserEntity storedUserDetail = userRepository.save(userEntity);
		BeanUtils.copyProperties(storedUserDetail, returnValue);

		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDTO getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		UserDTO returnValue = new UserDTO();
		BeanUtils.copyProperties(userEntity, returnValue);

		return returnValue;
	}

	@Override
	public UserDTO getUserByUserId(String userId) {
		
		UserDTO returnValue = new UserDTO();

		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if (userEntity == null) throw new UsernameNotFoundException(userId);
		
		BeanUtils.copyProperties(userEntity, returnValue);
		
		return returnValue;
	}

}
