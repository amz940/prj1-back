package com.example.prj1back.domain;

import com.example.prj1back.utill.AppUtil;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
    private List<String> fileNames;

    public String getAgo(){
        return AppUtil.getAgo(inserted, LocalDateTime.now());
    }

}
