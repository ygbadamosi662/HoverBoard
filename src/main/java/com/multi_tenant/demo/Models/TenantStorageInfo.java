package com.multi_tenant.demo.Models;

import com.multi_tenant.demo.Enums.DatabaseType;
import com.multi_tenant.demo.Enums.DbTestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
public class TenantStorageInfo
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private DatabaseType db_type;

    private String host_address;

    private String port_number;

    private String db_name;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private DbTestStatus testStatus;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "tenant_id")
    private User user;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "dime_id")
    private Dimension dime;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;
}
