package com.multi_tenant.demo.Services;

import com.multi_tenant.demo.Enums.Status;
import com.multi_tenant.demo.Models.*;
import com.multi_tenant.demo.Repos.CombosRepo;
import com.multi_tenant.demo.Repos.DimensionRepo;
import com.multi_tenant.demo.Repos.FeatureRepo;
import com.multi_tenant.demo.Repos.UserRepo;
import com.multi_tenant.demo.Utilities.Utility;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Getter
@Setter
@RequiredArgsConstructor
@Service
public class PortalService
{
    private final DimensionRepo dimensionRepo;

    private final UserRepo userRepo;

    private final FeatureRepo featureRepo;

    private final CombosRepo combosRepo;

    private final Utility util;

    public String generatePortalPrint(User tenant, Dimension dime)
    {
//        format: <tool-id?t"type"?s"sub">....
        if(tenant != null && dime != null)
        {
            if(tenant.if_i_have_dime(dime) == false)
            {
                return "";
            }
//            generating portal print
            String portal_print = "";

            for (Receipt receipt: tenant.getToolReceipts())
            {
                if(receipt.getStatus().equals(Status.ACTIVE) &&
                        receipt.getUsage_ends().isAfter(LocalDateTime.now()))
                {

                    String sub = receipt.getSub().name();
                    String typ = receipt.getType();
                    String uuid = receipt.getId().toString();
                    String format_ish = "<"+uuid+"?t\""+typ+"\"?s\""+sub+"\">";

                    portal_print = portal_print + format_ish;
                }
            }

            return portal_print;
        }
        return "";
    }

}
