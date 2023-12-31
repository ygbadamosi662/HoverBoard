package com.multi_tenant.demo.Models;


import com.multi_tenant.demo.Enums.Note;
import com.multi_tenant.demo.Enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Tool
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String type;

    private String name;

    private String version;

    @Column(length = 500)
    private String description;

//    if null, we own it
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "tool")
    private List<ToolReceipt> toolReceipts;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "dimension_id")
    private Dimension dime;
//    private String price;

    @Enumerated(EnumType.STRING)
    private Note note;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;
}
