package com.multi_tenant.demo.Repos;

import com.multi_tenant.demo.Enums.Status;
import com.multi_tenant.demo.Models.Dimension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DimensionRepo extends JpaRepository<Dimension, UUID>
{
    Slice<Dimension> findByStatus(Status status, Pageable pageable);
}
