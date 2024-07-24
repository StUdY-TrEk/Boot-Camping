package com.sparta.studytrek.domain.auth.entity;

public enum UserRoleEnum {

    USER(Authority.USER),  // 사용자 권한
    BOOTCAMP(Authority.BOOTCAMP), // 부트캠프 관리자 권한
    ADMIN(Authority.ADMIN);  // 관리자 권한

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getUserRole() {
        return this.authority;
    }

    public static UserRoleEnum getDefault() {
        return USER;
    }

    public static class Authority {
        public static final String USER = "USER";
        public static final String ADMIN = "ADMIN";
        public static final String BOOTCAMP = "BOOTCAMP";
    }

}