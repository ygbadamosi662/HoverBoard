package com.multi_tenant.demo.Repos;

import com.multi_tenant.demo.Enums.Status;
import com.multi_tenant.demo.Models.Contract;
import com.multi_tenant.demo.Models.Dimension;
import com.multi_tenant.demo.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ContractRepo extends JpaRepository<Contract, UUID>
{

    boolean existsByDimeAndUser(Dimension dime, User user);

    boolean existsByDimeAndUserAndStatus(Dimension dime, User user, Status status);
}
