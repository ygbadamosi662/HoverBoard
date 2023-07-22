package com.multi_tenant.demo.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
public class Combos extends Tool
{
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "portal_id")
    private Portal portal;

    @ManyToMany(mappedBy = "combos")
    private List<Feature> features;

    public Combos(){this.setType(Combos.class.getSimpleName());}

}
