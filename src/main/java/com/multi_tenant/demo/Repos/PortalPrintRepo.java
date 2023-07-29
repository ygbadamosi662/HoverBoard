package com.multi_tenant.demo.Repos;

import com.multi_tenant.demo.Models.Contract;
import com.multi_tenant.demo.Models.PortalPrint;
import com.multi_tenant.demo.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface PortalPrintRepo extends JpaRepository<PortalPrint, UUID>
{

    boolean existsByContractAndTenant(Contract con, User tenant);

    PortalPrint findByContractAndTenant(Contract con, User tenant);

    List<PortalPrint> findByTenant(User tenant);
}
