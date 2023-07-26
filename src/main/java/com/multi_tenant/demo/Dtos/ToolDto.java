package com.multi_tenant.demo.Dtos;

import com.multi_tenant.demo.Enums.Note;
import com.multi_tenant.demo.Models.Combos;
import com.multi_tenant.demo.Models.Feature;
import com.multi_tenant.demo.Models.Tool;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Setter
@Getter
public class ToolDto
{
    private String dime_id;
    @NotNull
    @NotBlank
    @Size(min=3, max=20)
    private String name;

    private String version;

    @NotNull
    @NotBlank
    @Size(min=20)
    private String description;

    @Enumerated(EnumType.STRING)
    private Note note;

    @NotNull
    @NotBlank
    private String type;


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

    public Feature getFeature()
    {
        if(this.validateVersion() == false)
        {
            return null;
        }
        Feature feat = new Feature();
        feat.setName(this.name);
        feat.setNote(this.note);
        feat.setVersion(this.version);
        feat.setDescription(this.description);
        feat.setType(this.type);

        return feat;
    }

    public Combos getCombo()
    {
        if(this.validateVersion() == false)
        {
            return null;
        }
        Combos combo = new Combos();
        combo.setName(this.name);
        combo.setNote(this.note);
        combo.setVersion(this.version);
        combo.setDescription(this.description);
        combo.setType(this.type);

        return combo;
    }

    public Tool getTool()
    {
        Tool me_or_you = new Tool();
        if(this.type.equals("feat"))
        {
            me_or_you  = this.getFeature();
        }

        if(this.type.equals("combo"))
        {
            me_or_you  = this.getCombo();
        }
        return me_or_you;
    }
}
