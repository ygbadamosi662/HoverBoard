package com.multi_tenant.demo.Repos;

import com.multi_tenant.demo.Models.Dimension;
import com.multi_tenant.demo.Models.Tool;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ToolRepo extends JpaRepository<Tool, UUID>
{

    Slice<Tool> findByType(String type, Pageable pageable);

    Slice<Tool> findByTypeAndDime(String type, Dimension dime, Pageable pageable);
}
