package com.multi_tenant.demo.Repos;

import com.multi_tenant.demo.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID>
{
    Optional<User> findByEmail(String email);
}
