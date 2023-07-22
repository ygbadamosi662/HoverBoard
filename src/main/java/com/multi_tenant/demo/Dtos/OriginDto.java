package com.multi_tenant.demo.Dtos;

import com.multi_tenant.demo.Enums.Protocol;
import com.multi_tenant.demo.Models.UserCors;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OriginDto
{
    private String protocol;

    private String ip_addy;

    private String port;

    public UserCors getUserCors()
    {
        UserCors userC = new UserCors();
        if (this.protocol.equals("HTTPS"))
        {
            userC.setProtocol(Protocol.HTTPS);
        }

        if (this.protocol.equals("HTTP"))
        {
            userC.setProtocol(Protocol.HTTP);
        }

        userC.setPort(this.port);

        userC.setIp_addy(this.ip_addy);

         return userC;
    }

    public String getOrigin()
    {
        return this.protocol.toLowerCase()+"://"+this.ip_addy+":"+this.port;
    }
}
