package com.multi_tenant.demo.Dtos;

import com.multi_tenant.demo.Enums.DatabaseType;
import com.multi_tenant.demo.Models.TenantStorageInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StorageDto
{

    @NotBlank
    @NotNull
    private String dime_id;

    @Enumerated(EnumType.STRING)
    private DatabaseType db_type;

    @NotBlank
    @NotNull
    private String host_address;

    @Pattern(regexp = "^\\d+$", message = "Must contain only digits")
    @Size(min = 5, max = 5)
    private String port_number;

    @NotBlank
    @NotNull
    private String db_name;

    @NotBlank
    @NotNull
    private String username;

    @NotBlank
    @NotNull
    private String password;

    public TenantStorageInfo getStorageInfo()
    {
        TenantStorageInfo storage = new TenantStorageInfo();
        storage.setUsername(this.username);
        storage.setPort_number(this.port_number);
        storage.setDb_name(this.db_name);
        storage.setDb_type(this.db_type);
        storage.setHost_address(this.host_address);

        return storage;
    }
}
