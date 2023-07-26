package com.multi_tenant.demo.Dtos;

import com.multi_tenant.demo.Enums.Subscription;
import com.multi_tenant.demo.Enums.Term;
import com.multi_tenant.demo.Models.Contract;
import com.multi_tenant.demo.Models.ToolReceipt;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscribeDto
{
    @NotNull
    @NotBlank
    private String dime_or_tool_id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Subscription sub;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Term term;

    @NotNull
    private int terms;

    public Contract getCon()
    {
        Contract con = new Contract();
        con.setSub(this.sub);
        con.setTerm(this.term);
        con.setTerms(this.terms);

        return con;
    }

    public ToolReceipt get_tool_receipt()
    {
        ToolReceipt tRex = new ToolReceipt();
        tRex.setSub(this.sub);
        tRex.setTerm(this.term);
        tRex.setTerms(this.terms);

        return tRex;
    }
}
