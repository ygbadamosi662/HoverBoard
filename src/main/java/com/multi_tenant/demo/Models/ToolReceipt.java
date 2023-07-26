package com.multi_tenant.demo.Models;

import com.multi_tenant.demo.Enums.Subscription;
import com.multi_tenant.demo.Enums.Term;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
public class ToolReceipt extends Receipt
{
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "tool_id")
    private Tool tool;

    public ToolReceipt(){this.setType(ToolReceipt.class.getSimpleName());}

}
