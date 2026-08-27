package com.genesys.dto;

import com.genesys.enums.user_enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreationRequest {

    private String username;

    private String password;

    private UserRole role;
}
