package com.multi_tenant.demo.Repos;

import com.multi_tenant.demo.Models.JwtBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface JwtBlackListRepo extends JpaRepository<JwtBlackList, Long>
{
    Optional<JwtBlackList> findByJwt(String token);
}
