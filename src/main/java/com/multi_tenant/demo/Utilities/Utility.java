package com.multi_tenant.demo.Utilities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Getter
@Setter
@Component
@RequiredArgsConstructor
public class Utility
{


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

}
