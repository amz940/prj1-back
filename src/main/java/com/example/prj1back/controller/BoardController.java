package com.example.prj1back.controller;

import com.example.prj1back.domain.Board;
import com.example.prj1back.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/board")
public class BoardController {

    private final BoardService service;

    @PostMapping("add")
    public ResponseEntity<Object> add(@RequestBody Board board)   {
        if (!service.validate(board)){
            return ResponseEntity.badRequest().build();
        }


       if (service.save(board)) {
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
