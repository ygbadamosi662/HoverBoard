package com.multi_tenant.demo.Models;

import com.multi_tenant.demo.Enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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
    private List<Contract> contracts;

    @OneToMany(mappedBy = "user")
    private List<ToolReceipt> toolReceipts;

    @OneToMany(mappedBy = "owner")
    private List<Tool> yours;

    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;

    public boolean if_i_have_dime(Dimension dime)
    {
//        checks if user is renting a dimension
        boolean if_ish = false;
        for (Contract con: this.getContracts())
        {
            if(con.getDime().equals(dime))
            {
                if_ish = true;
            }
        }
        return if_ish;
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
