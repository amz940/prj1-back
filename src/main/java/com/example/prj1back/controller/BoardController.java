package com.example.prj1back.controller;

import com.example.prj1back.domain.Board;
import com.example.prj1back.domain.Member;
import com.example.prj1back.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/board")
public class BoardController {

    private final BoardService service;

    @PostMapping("add")
    public ResponseEntity add(@RequestBody Board board,
                              @SessionAttribute(value = "login", required = false) Member login)   {

        if (login == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!service.validate(board)){
            return ResponseEntity.badRequest().build();
        }


       if (service.save(board, login)) {
           ResponseEntity.ok().build();
       } else {
           return ResponseEntity.internalServerError().build();
       }
        return null;
    }

    @GetMapping("list")
    public List<Board> list(){
        return service.list();
    }

    @GetMapping("id/{id}")
    public Board get(@PathVariable Integer id){
        return service.get(id);
    }

    @DeleteMapping("remove/{id}")
    public ResponseEntity remove(@PathVariable Integer id){
       if (service.remove(id)){
           return ResponseEntity.ok().build();
       } else {
            return ResponseEntity.internalServerError().build();
       }
    }

    @PutMapping("edit")
    public ResponseEntity edit(@RequestBody Board board) {
        if (service.validate(board)) {
            if (service.update(board)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
