package com.multi_tenant.demo.Models;

import com.multi_tenant.demo.Enums.Status;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
public class PortalPrint
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    private String print;

    @NotNull
    @OneToOne
    @JoinColumn(name = "cobtract_id")
    private Contract contract;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tenant_id")
    private User tenant;

    @ElementCollection(targetClass = ToolReceipt.class, fetch = FetchType.EAGER)
    private List<ToolReceipt> receipts;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;


    public boolean if_contract_active()
    {
//        returns true if contract stillactive
        return this.contract.getStatus().equals(Status.ACTIVE);
    }

    public boolean if_any_inactive_tool()
    {
//        returns false if this.receipts is null or no receipt is inactive
//        returns true if any receipt is inactive
//        false means no need for portal print rewrite, true means otherwise
        if(this.receipts != null)
        {
            return this.receipts.stream().anyMatch(rec -> rec.getStatus().equals(Status.INACTIVE));
        }
        return false;
    }

}
