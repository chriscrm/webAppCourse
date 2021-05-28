package com.crisr.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.crisr.entity.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
		UserEntity findByEmail(String email);
		UserEntity findByUserId(String userId);
		UserEntity findUserByEmailVerificationToken(String token);
}
