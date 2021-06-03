package com.crisr.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.crisr.entity.PasswordResetTokenEntity;

@Repository
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {

}
