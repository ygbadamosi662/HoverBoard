package com.multi_tenant.demo.Utilities;

import com.multi_tenant.demo.Enums.Status;
import com.multi_tenant.demo.Models.Receipt;
import com.multi_tenant.demo.Repos.ReceiptRepo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Getter
@Setter
@Component
@RequiredArgsConstructor
public class Utility
{

    private final ReceiptRepo receiptRepo;

    public boolean checkValidUrl(String url)
    {
        // Regular expression pattern for URL validation
        String regex = "^(http|https)://([a-zA-Z0-9.-]+)(:[0-9]+)?([/a-zA-Z0-9.-]*)?$";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(regex);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(url);

        // Check if the URL matches the pattern
        return matcher.matches();
    }

    public LocalDateTime get_tool_usage_end(Receipt receipt)
    {
        int val = Integer.parseInt(receipt.getTerm().getValue());
        int multiplier = receipt.getTerms();
        LocalDateTime now = LocalDateTime.now();

        return now.plusDays(val * multiplier);
    }

    public Receipt deactivate_receipt(Receipt receipt)
    {
        receipt.setStatus(Status.INACTIVE);
        return receiptRepo.save(receipt);
    }

}
