package com.multi_tenant.demo.Repos;

import com.multi_tenant.demo.Enums.Status;
import com.multi_tenant.demo.Models.Tool;
import com.multi_tenant.demo.Models.ToolReceipt;
import com.multi_tenant.demo.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ToolReceiptRepo extends JpaRepository<ToolReceipt, UUID>
{

    boolean existsByUserAndTool(User user, Tool tool);

    boolean existsByUserAndToolAndStatus(User user, Tool tool, Status status);
}
