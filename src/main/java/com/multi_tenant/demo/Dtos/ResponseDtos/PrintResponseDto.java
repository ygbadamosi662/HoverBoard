package com.multi_tenant.demo.Dtos.ResponseDtos;

import com.multi_tenant.demo.Models.PortalPrint;
import com.multi_tenant.demo.Models.ToolReceipt;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class PrintResponseDto
{
    private String print;

    private ReceiptResponseDto contract;

    private List<ReceiptResponseDto> receipts;

    public PrintResponseDto(PortalPrint p_print)
    {
        this.contract = new ReceiptResponseDto(p_print.getContract());
        this.print = p_print.getPrint();
    }

    public void set_receipts(List<ToolReceipt> receipts)
    {
        this.receipts = receipts
                .parallelStream()
                .map(rec -> new ReceiptResponseDto(rec))
                .collect(Collectors.toList());
    }
}
