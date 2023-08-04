package com.multi_tenant.demo.Services;

import com.multi_tenant.demo.Enums.Status;
import com.multi_tenant.demo.Models.*;
import com.multi_tenant.demo.Repos.*;
import com.multi_tenant.demo.Utilities.Utility;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
@Setter
@RequiredArgsConstructor
@Service
public class PortalService
{
    //        format: <tenant-id?t"type"?s"sub"><tool-id?t"type"?s"sub"><tool-id?t"type"?s"sub">....
    private final DimensionRepo dimensionRepo;

    private final UserRepo userRepo;

    private final FeatureRepo featureRepo;

    private final ContractRepo contractRepo;

    private final ToolReceiptRepo toolReceiptRepo;
    private final ReceiptRepo receiptRepo;

    private final PortalPrintRepo printRepo;

    private final CombosRepo combosRepo;

    private final Utility util;

    private String deli = "666";


    public String print_receipt(ToolReceipt receipt)
    {
//      returns an empty string if receipt is null
//        returns a formatted string otherwise
        if(receipt != null)
        {
            String sub = receipt.getSub().name();
            String typ = receipt.getTool().getType();
            String uuid = receipt.getId().toString();
            String print = String.format("<%s?t=\"%s\"?s=\"%s\">", uuid, typ, sub);
//            String print = "<" + uuid + "?t=\"" + typ + "\"?s=\"" + sub + "\">";

            return print;
        }
        return "";
    }

    public String print_receipts(List<ToolReceipt> receipts)
    {
//        returns an empty string if receipts is null
//        returns a formatted string otherwise
        if(receipts != null)
        {
            String prints = "";
            List<String> rec_print = receipts
                    .parallelStream()
                    .map(rec -> this.print_receipt(rec))
                    .collect(Collectors.toList());

            prints += String.join(deli, rec_print);
            return prints;
        }
        return "";
    }

    public String print_contract(Contract receipt)
    {
//        returns an empty string if receipt is null
//        returns a formatted string otherwise
        if(receipt != null)
        {
            String sub = receipt.getSub().name();
            String uuid = receipt.getId().toString();
            String print = String.format("<%s?s=\"%s\">", uuid, sub);
//            String print = "<"+uuid+"?s=\""+sub+"\">";

            return print;
        }
        return "";
    }

    public String print_tenant(User user)
    {
//        returns an empty string if user is null
//        returns a formatted string otherwise
        if(user != null)
        {
            String uuid = user.getId().toString();
            String print = "<"+uuid+">";

            return print;
        }
        return "";
    }

    public String generate_portal_print(PortalPrint print)
    {
//        returns an empty string if print is null
//        returns a formatted string otherwise
//        Always make sure the parameter print has only active tools and contracts, it does not check
        if(print == null) return "";

        String con = this.print_contract(print.getContract());
        String ten = this.print_tenant(print.getTenant());
        String prints = ten + deli + con;
        if(!print.getReceipts().isEmpty()) prints += deli + this.print_receipts(print.getReceipts());

        return prints;
    }

    public List<ToolReceipt> get_active_receiptsByDime(List<ToolReceipt> receipts, Dimension dime)
    {
        if(receipts != null)
        {
           return receipts
                    .parallelStream()
                    .filter(receipt -> receipt.getTool().getDime().equals(dime))
                    .filter(receipt -> receipt.getStatus().equals(Status.ACTIVE))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public PortalPrint get_new_persited_print(User tenant, Contract con)
    {
//        returns null if tenant and con is null
//        returns a persited instance of PortalPrint
        if(tenant != null && con != null)
        {
            PortalPrint newPrint = new PortalPrint();
            newPrint.setTenant(tenant);
            newPrint.setContract(con);
            newPrint.setStatus(Status.ACTIVE);
            if(tenant.getToolReceipts() != null)
            {
                newPrint.setReceipts(this.get_active_receiptsByDime(tenant.getToolReceipts(),
                         con.getDime()));
            }
            newPrint.setPrint(this.generate_portal_print(newPrint));

            return printRepo.save(newPrint);
        }
        return null;
    }

    public List<Contract> update_tenant_contracts(User tenant)
    {
        if(tenant == null) return null;

        return tenant.get_expired_contracts()
                .parallelStream()
                .map((con) -> {
                    con.setStatus(Status.INACTIVE);
                    return contractRepo.save(con);
                })
                .collect(Collectors.toList());
    }

    public List<ToolReceipt> update_tenant_toolReceipts(User tenant)
    {
        if(tenant == null) return null;

        return tenant.get_expired_toolReceipts()
                .parallelStream()
                .map((rec) -> {
                    rec.setStatus(Status.INACTIVE);
                    return toolReceiptRepo.save(rec);
                })
                .collect(Collectors.toList());
    }

    public List<Receipt> update_all_tenant_receipts(User tenant)
    {
//        returns null if tenant is null
//        returns the updated and persisted tenant receipts
        if(tenant != null)
        {
            List<Receipt> receipts = tenant.get_expired_receipts();
            return receipts
                    .parallelStream()
                    .map((receipt) -> {
                        receipt.setStatus(Status.INACTIVE);
                        return receiptRepo.save(receipt);
                    })
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<Receipt> update_tenant_portfolio(User tenant)
    {
//        returns null if tenant is null
//        returns an empty list of no receipt needs update
//        returns a list of receipts otherwise
        if(tenant == null) return null;

        List<Boolean> true_true = new ArrayList<>(Arrays.asList(false, false));

//        if tenant have expired contract
        if(tenant.chk_for_expired_contract()) true_true.set(0, true);
//        if tenant have expired toolReceipt
        if(tenant.chk_for_expired_toolReceipt()) true_true.set(1, true);

//        returns all expired receipts, contracts and toolReceipts
        if(true_true.get(0) && true_true.get(0)) return this.update_all_tenant_receipts(tenant);

        List<Receipt> receipts = new ArrayList<>();

//        if no update is needed
        if((true_true.get(0) == false) && (true_true.get(1) == false)) return receipts;

//        gets expired contracts
        if(true_true.get(0))
        {
            receipts.addAll(this.update_tenant_contracts(tenant));
        }
//        gets expired toolReceipts
        if(true_true.get(1))
        {
            receipts.addAll(this.update_tenant_toolReceipts(tenant));
        }

        return receipts;



    }



}
