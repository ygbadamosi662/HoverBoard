package com.multi_tenant.demo.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


@Setter
@Getter
@Entity
public class Feature extends Tool
{
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "portal_id")
    private Portal portal;

    @ManyToMany
    @JoinTable(
            name = "concerns",
            joinColumns = @JoinColumn(name = "feature_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "combos_id", referencedColumnName = "id")
    )
    private List<Combos> combos;

    @OneToMany
    private List<Feature> dependencies;

    public Feature(){this.setType(Feature.class.getSimpleName());}

}
