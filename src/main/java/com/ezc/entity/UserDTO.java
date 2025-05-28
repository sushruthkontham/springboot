package com.ezc.entity;


import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private String email;
    private String role;
    private List<String> permissions;
}
