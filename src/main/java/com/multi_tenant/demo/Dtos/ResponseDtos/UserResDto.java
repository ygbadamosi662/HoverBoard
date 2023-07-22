package com.multi_tenant.demo.Dtos.ResponseDtos;

import com.multi_tenant.demo.Models.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserResDto extends ResponseDto
{
    private String email;

    private String phone;

    public UserResDto(User user)
    {
        this.email = user.getEmail();
        this.phone = user.getPhone();
    }
}
