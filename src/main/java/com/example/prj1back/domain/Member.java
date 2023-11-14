package com.example.prj1back.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Member {
    private String id;
    private String password;
    private String email;
    private String nickName;
    private LocalDateTime inserted;
    private List<Auth> auth;
}
