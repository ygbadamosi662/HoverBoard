package com.multi_tenant.demo.Repos;

import com.multi_tenant.demo.Models.ToolReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ToolReceiptRepo extends JpaRepository<ToolReceipt, UUID>
{

}
