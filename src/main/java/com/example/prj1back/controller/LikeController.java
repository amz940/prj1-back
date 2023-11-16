package com.example.prj1back.controller;

import com.example.prj1back.domain.Like;
import com.example.prj1back.domain.Member;
import com.example.prj1back.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
public class LikeController {

    private final LikeService service;

    @PostMapping
    public ResponseEntity<Object> like(@RequestBody Like like,
                                       @SessionAttribute(value = "login", required = false)Member login ){
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        service.update(like, login);
        return null;
    }
}
