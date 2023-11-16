package com.example.prj1back.controller;

import com.example.prj1back.domain.Comment;
import com.example.prj1back.domain.Member;
import com.example.prj1back.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService service;

    @PostMapping("add")
    public ResponseEntity add(@RequestBody Comment comment,
                              @SessionAttribute(value = "login", required = false) Member login){

        if (login == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if ( service.validate(comment)) {
            if (service.add(comment, login)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }
        }
        return null;
    }

    @GetMapping("list")
    public List<Comment> list(@RequestParam("id") Integer boardId){
        return service.list(boardId);
    }

    @DeleteMapping("{id}")
    public void remove(@PathVariable Integer id) {
        // TODO : 권한 검증 코드


        service.remove(id);
    }

    @PutMapping("edit")
    public void update(@RequestBody Comment comment) {
        service.update(comment);
    }
}
