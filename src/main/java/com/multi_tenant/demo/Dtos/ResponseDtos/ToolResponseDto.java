package com.multi_tenant.demo.Dtos.ResponseDtos;

import com.multi_tenant.demo.Enums.Note;
import com.multi_tenant.demo.Enums.Status;
import com.multi_tenant.demo.Models.Tool;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
@RequiredArgsConstructor
@Getter
public class ToolResponseDto
{
    private String id;

    private String type;

    private String name;

    private String version;

    private String description;

    private String owner;

    @Enumerated(EnumType.STRING)
    private Note note;

    @Enumerated(EnumType.STRING)
    private Status status;

    public ToolResponseDto(Tool tool)
    {
        this.id = tool.getId().toString();
        this.type = tool.getType();
        this.name = tool.getName();
        this.description = tool.getDescription();
        this.version = tool.getVersion();
        this.note = tool.getNote();
        this.status = tool.getStatus();
        this.owner = tool.getOwner() != null ? tool.getOwner().getEmail() : "Our business email";
    }
}
