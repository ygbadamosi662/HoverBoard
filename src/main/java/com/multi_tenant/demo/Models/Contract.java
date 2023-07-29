package com.multi_tenant.demo.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
public class Contract extends Receipt
{
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "dime_id")
    private Dimension dime;

    @OneToOne(mappedBy = "contract")
    private PortalPrint print;

    public Contract(){this.setType(Contract.class.getSimpleName());}

}
