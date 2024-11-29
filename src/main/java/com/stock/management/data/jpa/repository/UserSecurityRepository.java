package com.stock.management.data.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.management.data.jpa.model.UserInfo;

public interface UserSecurityRepository extends JpaRepository<UserInfo, String> {
    Optional<UserInfo> findByUserId(String userId);
}

