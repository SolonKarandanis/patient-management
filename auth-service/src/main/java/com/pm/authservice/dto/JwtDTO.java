package com.pm.authservice.dto;

import java.util.Date;

public class JwtDTO {
    private String token;
    private Date expires;

    public JwtDTO(String token, Date expires) {
        this.expires = expires;
        this.token = token;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return the expires
     */
    public Date getExpires() {
        return expires;
    }

    /**
     * @param expires the expires to set
     */
    public void setExpires(Date expires) {
        this.expires = expires;
    }
}
