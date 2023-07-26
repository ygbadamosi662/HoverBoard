package com.multi_tenant.demo.Repos;

import com.multi_tenant.demo.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID>
{
    Optional<User> findByEmail(String email);

//    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.phone.status = :status")
//    boolean existsByStatus(@Param("status") String status);
}
