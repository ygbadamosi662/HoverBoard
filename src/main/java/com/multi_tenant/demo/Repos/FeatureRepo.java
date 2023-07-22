package com.multi_tenant.demo.Repos;

import com.multi_tenant.demo.Models.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeatureRepo extends JpaRepository<Feature, UUID>
{

}
