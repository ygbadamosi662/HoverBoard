package com.multi_tenant.demo.Repos;

import com.multi_tenant.demo.Models.TenantStorageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TenantStorageInfoRepo extends JpaRepository<TenantStorageInfo, UUID>
{

}
