package com.prjratingsystem.dto;

import com.prjratingsystem.model.Role;
import lombok.Data;

@Data
public class UserRegistrationDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}