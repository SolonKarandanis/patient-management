package com.pm.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    private Integer id;
    private String name;
    private String nameLabel;

    public RoleDTO(Integer id,String name){
        this.id = id;
        this.name = name;
    }
}
