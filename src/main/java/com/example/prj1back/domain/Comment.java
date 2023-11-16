package com.example.prj1back.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Integer id;
    private Integer boardId;
    private String memberId;
    private String comment;
    private String memberNickName;
    private LocalDateTime inserted;
}
