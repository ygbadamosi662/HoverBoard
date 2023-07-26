package com.multi_tenant.demo.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
@Entity
public class Feature extends Tool
{
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
