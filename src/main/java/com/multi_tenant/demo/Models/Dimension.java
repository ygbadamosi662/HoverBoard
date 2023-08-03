package com.multi_tenant.demo.Models;

import com.multi_tenant.demo.Enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
public class Dimension {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column( name="dimension_id")
    private UUID id;

    private String name;

    private String version;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "dime")
    List<Tool> tools;

    @OneToMany(mappedBy = "dime")
    List<Contract> contracts;

    @OneToMany(mappedBy = "dime")
    List<TenantStorageInfo> storageInfos;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;

}
