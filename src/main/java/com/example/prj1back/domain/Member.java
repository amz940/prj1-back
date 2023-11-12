package com.example.prj1back.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Member {
    private String id;
    private String password;
    private String email;
    private LocalDateTime inserted;
}
