package com.multi_tenant.demo.Dtos.ResponseDtos;

import com.multi_tenant.demo.Models.PortalPrint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DimeApiResponseDto
{
    private String tenant_id;

    private String portal_print;

    public DimeApiResponseDto(PortalPrint print)
    {
        this.tenant_id = print.getTenant().getId().toString();
        this.portal_print = print.getPrint();
    }
}
