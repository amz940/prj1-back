package com.example.prj1back.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Board {
    private Integer id;
    private String title;
    private String content;
    private String writer;
    private String nickName;
    private Integer countComment;
    private Integer countLike;
    private LocalDateTime inserted;
}
