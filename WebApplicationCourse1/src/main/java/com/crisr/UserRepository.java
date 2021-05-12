package com.crisr;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.crisr.entity.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
		UserEntity findByEmail(String email);
		UserEntity findByUserId(String userId);
}
