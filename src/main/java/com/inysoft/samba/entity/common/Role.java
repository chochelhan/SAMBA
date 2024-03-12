package com.inysoft.samba.entity.common;

import lombok.Getter;

@Getter
public enum Role {

    ROLE_ADMIN("admin"), ROLE_MANAGER("manager"), ROLE_MEMBER("user");

    private String description;

    Role(String description) {
        this.description = description;
    }
}
