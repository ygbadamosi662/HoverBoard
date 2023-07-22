package com.multi_tenant.demo.Models;

import com.multi_tenant.demo.Enums.Subscription;
import com.multi_tenant.demo.Enums.Term;
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
public class Contract
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column( name="contract_id")
    private UUID id;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "tool_id")
    private Tool tool;

    @Enumerated(EnumType.STRING)
    private Subscription sub;

    @Enumerated(EnumType.STRING)
    private Term term;

    private int contract_lenght;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;

    private LocalDateTime contract_ends;
}
