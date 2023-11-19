package com.example.prj1back.domain;

import com.example.prj1back.utill.AppUtil;
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

    public String getAgo(){
        return AppUtil.getAgo(inserted, LocalDateTime.now());
    }
}
