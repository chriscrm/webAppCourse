package com.crisr.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crisr.dto.AddressDTO;
import com.crisr.entity.AddressEntity;
import com.crisr.entity.UserEntity;
import com.crisr.repository.AddressRepository;
import com.crisr.repository.UserRepository;
import com.crisr.service.AddressService;

@Service
public class AddressServiceImpl implements AddressService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AddressRepository addressRepository;

	@Override
	public List<AddressDTO> getAddresses(String userId) {

		List<AddressDTO> returnValue = new ArrayList<>();
		ModelMapper modelMapper = new ModelMapper();
		
		//fetching user by userId using autowire userRepository
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if(userEntity == null) return returnValue;
		
		Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
		
		for (AddressEntity addressEntity : addresses) {
			returnValue.add(modelMapper.map(addressEntity, AddressDTO.class));
		}
		
		return returnValue;
	}

}
