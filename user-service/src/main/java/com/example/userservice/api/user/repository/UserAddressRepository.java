package com.example.userservice.api.user.repository;

import com.example.userservice.api.user.domain.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

}
