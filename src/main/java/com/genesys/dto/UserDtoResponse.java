package com.genesys.dto;

import com.genesys.entity.User;
import com.genesys.enums.user_enums.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserDtoResponse {

    private String id;
    private String username;
    private UserRole role;
    private boolean active;

}
