package com.example.prj1back.controller;

import com.example.prj1back.domain.Like;
import com.example.prj1back.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
public class LikeController {

    private final LikeService service;

    @PostMapping
    public void like(@RequestBody Like like){
        service.update(like);
    }
}
