package com.multi_tenant.demo.Dtos;

import com.multi_tenant.demo.Models.Dimension;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Setter
@Getter
public class DimeDto
{
    @NotNull
    @NotBlank
    @Size(min=3, max=20)
    private String name;

    private String version;

    @NotNull
    @NotBlank
    @Size(min=20)
    private String description;

    public boolean validateVersion()
    {
        String versionRegex = "^\\d+(\\.\\d+)*$";

        Pattern pattern = Pattern.compile(versionRegex);
        Matcher matcher = pattern.matcher(this.version);

        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public Dimension getDime()
    {
        if(this.validateVersion() == false)
        {
            return null;
        }
        Dimension dime = new Dimension();
        dime.setName(this.name);
        dime.setVersion(this.version);
        dime.setDescription(this.description);

        return dime;
    }
}
