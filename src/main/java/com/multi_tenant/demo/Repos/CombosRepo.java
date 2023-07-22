package com.multi_tenant.demo.Repos;

import com.multi_tenant.demo.Models.Combos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CombosRepo extends JpaRepository<Combos, UUID>
{

}
