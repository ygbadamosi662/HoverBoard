package com.multi_tenant.demo.Models;


import com.multi_tenant.demo.Enums.Protocol;
import com.multi_tenant.demo.Enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
public class UserCors {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Protocol protocol;

    private String ip_addy;

    private String port;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;

    public String getOrigin()
    {
        return this.protocol.getValue()+"://"+this.ip_addy+":"+this.port;
    }
}
