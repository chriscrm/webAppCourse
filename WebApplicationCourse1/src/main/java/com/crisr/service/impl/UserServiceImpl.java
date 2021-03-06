package com.crisr.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.crisr.CustomUtils;
import com.crisr.dto.AddressDTO;
import com.crisr.dto.UserDTO;
import com.crisr.entity.PasswordResetTokenEntity;
import com.crisr.entity.UserEntity;
import com.crisr.exceptions.UserServiceException;
import com.crisr.model.response.ErrorMessages;
import com.crisr.repository.PasswordResetTokenRepository;
import com.crisr.repository.UserRepository;
import com.crisr.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	CustomUtils customUtils;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;

	@Override
	public UserDTO createUser(UserDTO user) {

		//validating if user email exists in the DB
		if (userRepository.findByEmail(user.getEmail()) != null)
			throw new RuntimeException("User Email already exists");
		
		//setting public addressId for each address received
		for (int i = 0; i < user.getAddresses().size(); i++) {
			AddressDTO address = user.getAddresses().get(i);
			address.setUserDetails(user);
			address.setAddressId(customUtils.generateAddressId(10));
			user.getAddresses().set(i, address);
		}
			

		//UserEntity userEntity = new UserEntity();
		//BeanUtils.copyProperties(user, userEntity);
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);
		
		
		// Generating a random public userId and password encryption
		String publicUserId = customUtils.generateUserId(15);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		//creating emailToken using customUtils and takes userId parameter
		userEntity.setEmailVerificationToken(CustomUtils.generateEmailVerificationToken(publicUserId));
		userEntity.setEmailVerificationStatus(false);
		
		//UserDTO returnValue = new UserDTO();

		UserEntity storedUserDetail = userRepository.save(userEntity);
		//BeanUtils.copyProperties(storedUserDetail, returnValue);
		UserDTO returnValue = modelMapper.map(storedUserDetail, UserDTO.class);
		
		/*
		 * Here Send and email message to user to verify their email address
		 * for example using AmazonSES
		 * new AmazonSES().verifyEmail(returnValue);
		 */
		
		return returnValue;
	}

	/**
	 * @param user email.
	 * @throws UsernameNotFoundException if a <code>null</code> value is returned by
	 * findByEmail parameter when the email's not founds
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		//this allows login user with email and password
		//return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
		
		//this allows login with user if enable
		return new User(userEntity.getEmail(), 
						userEntity.getEncryptedPassword(), 
						userEntity.getEmailVerificationStatus(),
						true, true, true, new ArrayList<>());
		
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

		if (userEntity == null)
			throw new UsernameNotFoundException(userId);

		BeanUtils.copyProperties(userEntity, returnValue);

		return returnValue;
	}

	@Override
	public UserDTO updateUser(String userId, UserDTO userDTO) {

		UserDTO returnValue = new UserDTO();
		UserEntity userEntity = userRepository.findByUserId(userId);

		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		//only fields that wants to update
		userEntity.setFirstName(userDTO.getFirstName());
		userEntity.setLastName(userDTO.getLastName());

		UserEntity updatedUser = userRepository.save(userEntity);
		BeanUtils.copyProperties(updatedUser, returnValue);

		return returnValue;
	}

	@Override
	public void deleteUser(String userId) {

		UserEntity userEntity = userRepository.findByUserId(userId);

		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		userRepository.delete(userEntity);

	}

	@Override
	public List<UserDTO> getUsers(int page, int limit) {

		List<UserDTO> returnValue = new ArrayList<>();

		if (page > 0)
			page -= 1;

		Pageable pageableRequest = PageRequest.of(page, limit);

		Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);

		List<UserEntity> users = usersPage.getContent();

		for (UserEntity userEntity : users) {
			UserDTO userDTO = new UserDTO();
			BeanUtils.copyProperties(userEntity, userDTO);
			returnValue.add(userDTO);
		}

		return returnValue;
	}

	@Override
	public boolean verifyEmailToken(String token) {
		
		boolean returnValue = false;

		//find user by token
		UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);
		
		if(userEntity != null) {
			
			boolean hasTokenExpired = CustomUtils.hasTokenExpired(token);
			
			if(!hasTokenExpired) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				
				userRepository.save(userEntity);
				returnValue = true;
			}
			
		}
		
		
		return returnValue;
	}

	@Override
	public boolean requestPasswordReset(String email) {
		
		boolean returnValue = false;

		UserEntity userEntity = userRepository.findByEmail(email);
		
		if (userEntity == null) {
			return returnValue;
		}
		
		//Generate new reset roken
		String token = CustomUtils.generatePasswordResetToken(userEntity.getUserId());
		
		//New Entity PasswordResetTokenEntity
		PasswordResetTokenEntity passwordEntity = new PasswordResetTokenEntity();
		
		passwordEntity.setToken(token);
		passwordEntity.setUserDetails(userEntity);
		
		passwordResetTokenRepository.save(passwordEntity);
		
		//send email token verification for example:
		/*
		 * returnValue = new AmazonSES().sendPasswordResetRequest(
		 * userEntity.getFirstName(), userEntity.getEmail(), token);
		 */
		
		return returnValue;
	}

}
