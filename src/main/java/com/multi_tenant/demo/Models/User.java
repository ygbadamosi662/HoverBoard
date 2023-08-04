package com.multi_tenant.demo.Models;

import com.multi_tenant.demo.Enums.Role;
import com.multi_tenant.demo.Enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Setter
@Getter
@Entity
public class User implements UserDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String password;

    @Column(unique = true)
    private String email;

//    @Column(unique = true)
    private String phone;

    @OneToMany(mappedBy = "user")
    private List<UserCors> userCors;

    @OneToMany(mappedBy = "user")
    private List<TenantStorageInfo> storages;

    @OneToMany(mappedBy = "user")
    private List<Contract> contracts;

    @OneToMany(mappedBy = "user")
    private List<ToolReceipt> toolReceipts;

    @OneToMany(mappedBy = "owner")
    private List<Tool> yours;

    @OneToMany(mappedBy = "tenant")
    private List<PortalPrint> prints;

    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;

    public boolean if_i_have_dime(Dimension dime)
    {
//        checks if user have an active contract with dime
        if(dime != null)
        {
            return this.getContracts()
                    .parallelStream()
                    .anyMatch(con -> con.getDime().equals(dime) && con.getDime()
                            .getStatus().equals(Status.ACTIVE));
        }
        return false;
    }

    public boolean if_i_have_tool(Tool tool)
    {
//        checks if user have an active sub for tool
        if(tool != null)
        {
            return this.getToolReceipts()
                    .parallelStream()
                    .anyMatch(rec -> rec.getTool().equals(tool) && rec.getStatus().equals(Status.ACTIVE));
        }
        return false;
    }

    public Contract get_contractByDime(Dimension dime)
    {
        if(dime == null) return null;

        List<Contract> cons = this.getContracts()
                .stream()
                .filter(con -> con.getDime().equals(dime))
                .collect(Collectors.toList());
        return cons.get(0);
    }

    public ToolReceipt get_toolByDime(Dimension dime)
    {
        if(dime == null) return null;

        List<ToolReceipt> recs = this.getToolReceipts()
                .parallelStream()
                .filter(rec -> rec.getTool().getDime().equals(dime))
                .collect(Collectors.toList());

        return recs.get(0);
    }

    public List<Receipt> get_expired_receipts()
    {
        LocalDateTime now = LocalDateTime.now();
        List<Receipt> receipts = this.toolReceipts
                .parallelStream()
                .filter(receipt -> receipt.getUsage_ends().isBefore(now))
                .collect(Collectors.toList());

        receipts.addAll(
                this.getContracts()
                        .parallelStream()
                        .filter(con -> con.getUsage_ends().isBefore(now))
                        .collect(Collectors.toList())
                );

        return receipts;
    }

    public boolean chk_for_expired_toolReceipt()
    {
        if(this.toolReceipts.isEmpty()) return false;

        return this.toolReceipts
                    .parallelStream()
                    .anyMatch(rec -> rec.getUsage_ends().isBefore(LocalDateTime.now()));
    }

    public boolean chk_for_expired_contract()
    {
        if(this.contracts.isEmpty()) return false;

        return this.contracts
                .parallelStream()
                .anyMatch(rec -> rec.getUsage_ends().isBefore(LocalDateTime.now()));
    }

    public List<Contract> get_expired_contracts()
    {
//        returns null if this.contracts is empty
//        returns contracts that their usage_ends has passed
        if(this.contracts.isEmpty()) return null;

        return this.contracts
                .parallelStream()
                .filter(con -> con.getUsage_ends().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    public List<ToolReceipt> get_expired_toolReceipts()
    {
//        returns null if this.toolReceipts is empty
//        returns toolReceipts that their usage_ends has passed
        if(this.toolReceipts.isEmpty()) return null;

        return this.toolReceipts
                .parallelStream()
                .filter(con -> con.getUsage_ends().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.name()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
