package com.prjratingsystem.dto;

import com.prjratingsystem.model.Role;
import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}