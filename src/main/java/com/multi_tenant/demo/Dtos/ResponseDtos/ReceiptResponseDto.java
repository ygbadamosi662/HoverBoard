package com.multi_tenant.demo.Dtos.ResponseDtos;

import com.multi_tenant.demo.Enums.Status;
import com.multi_tenant.demo.Enums.Subscription;
import com.multi_tenant.demo.Enums.Term;
import com.multi_tenant.demo.Models.Receipt;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReceiptResponseDto extends ResponseDto
{
    private String id;

    private String type;

    private String user;

    @Enumerated(EnumType.STRING)
    private Subscription sub;

    @Enumerated(EnumType.STRING)
    private Term term;

    private int terms;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String usage_ends;

    public ReceiptResponseDto (Receipt recipe)
    {
        this.id = recipe.getId().toString();
        this.type = recipe.getType();
        this.user = recipe.getUser().getEmail();
        this.sub = recipe.getSub();
        this.term = recipe.getTerm();
        this.terms = recipe.getTerms();
        this.status = recipe.getStatus();
        this.usage_ends = this.getStringDateTime(recipe.getUsage_ends());
    }
}
