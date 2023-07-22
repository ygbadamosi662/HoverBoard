package com.multi_tenant.demo.Repos;

import com.multi_tenant.demo.Models.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContractRepo extends JpaRepository<Contract, UUID>
{

}
