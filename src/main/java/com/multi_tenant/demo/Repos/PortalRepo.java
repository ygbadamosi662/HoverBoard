package com.multi_tenant.demo.Repos;

import com.multi_tenant.demo.Models.Portal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PortalRepo extends JpaRepository<Portal, UUID>
{

}
