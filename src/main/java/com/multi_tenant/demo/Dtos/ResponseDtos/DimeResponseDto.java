package com.multi_tenant.demo.Dtos.ResponseDtos;

import com.multi_tenant.demo.Enums.Status;
import com.multi_tenant.demo.Models.Dimension;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;


@Getter
public class DimeResponseDto extends ResponseDto
{
    private String id;
    private String name;

    private String version;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    public DimeResponseDto(Dimension dime)
    {
        this.id = dime.getId().toString();
        this.name = dime.getName();
        this.version = dime.getVersion();
        this.description = dime.getDescription();
        this.status = dime.getStatus();
    }
}
