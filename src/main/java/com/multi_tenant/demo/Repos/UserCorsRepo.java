package com.multi_tenant.demo.Repos;


import com.multi_tenant.demo.Models.UserCors;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserCorsRepo extends JpaRepository<UserCors, UUID>
{

}
