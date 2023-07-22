package com.multi_tenant.demo.Dtos;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class LoginDto
{
    private String email;

    private String password;
}
