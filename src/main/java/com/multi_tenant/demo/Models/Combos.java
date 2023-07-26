package com.multi_tenant.demo.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class Combos extends Tool
{
    @ManyToMany(mappedBy = "combos")
    private List<Feature> features;

    public Combos(){this.setType(Combos.class.getSimpleName());}

}
