package com.multi_tenant.demo.Models;

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
public class Portal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column( name="portal_id")
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "portal")
    List<Feature> features;

    @OneToMany(mappedBy = "portal")
    List<Feature> combos;

    @ManyToMany
    @JoinTable(
            name = "Agreement",
            joinColumns = @JoinColumn(name = "portal_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;

}
