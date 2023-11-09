package com.example.prj1back.controller;

import com.example.prj1back.domain.Board;
import com.example.prj1back.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/board")
public class BoardController {

    private final BoardService service;

    @PostMapping("add")
    public void add(@RequestBody Board board)   {
        service.save(board);
    }
}
