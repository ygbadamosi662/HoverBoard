package com.multi_tenant.demo.Dtos;

import com.multi_tenant.demo.Enums.Role;
import com.multi_tenant.demo.Models.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto
{
    @Size(min = 8,max = 15)
    @NotBlank
    @NotNull
    private String password;

    @Size(min=11) @Email(message = "Must follow this pattern 'dfshghf@hdfgf.com'",
            regexp = "[a-z]{2,10}@[a-z]{2,10}\\.[a-z]{2,10}")
    private String email;

    @NotNull
    @NotBlank
    @Size(min=10,max=10)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User getUser ()
    {
        User user = new User();
        user.setEmail(this.email);
        user.setRole(this.role);
        user.setPassword(this.password);
        user.setPhone(this.phone);

        return user;
    }
}
