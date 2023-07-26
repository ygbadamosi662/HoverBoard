package com.multi_tenant.demo.Models;

import com.multi_tenant.demo.Enums.Product;
import com.multi_tenant.demo.Enums.Status;
import com.multi_tenant.demo.Enums.Subscription;
import com.multi_tenant.demo.Enums.Term;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Receipt
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String type;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Subscription sub;

    @Enumerated(EnumType.STRING)
    private Term term;

    private int terms;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;

    private LocalDateTime usage_ends;

}
