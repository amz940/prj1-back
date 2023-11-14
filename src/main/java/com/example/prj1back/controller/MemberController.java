package com.example.prj1back.controller;

import com.example.prj1back.domain.Member;
import com.example.prj1back.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/member")
public class MemberController {
    private  final MemberService service;

    @PostMapping("signup")
    public void signup(@RequestBody Member member) {
        service.add(member);
    }

    @GetMapping(value = "check", params = "id")
    public ResponseEntity checkId(String id) {
        if (service.getId(id) == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping(value = "check", params = "email")
    public ResponseEntity checkEmail(String email){
        if (service.getEmail(email) == null){
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping("list")
    public List<Member> list(){
        return service.list();
    }

    @GetMapping
    public ResponseEntity<Member> view(String id) {
        // TODO : 로그인 했는지 검사 -> error 시 401
        // TODO : 자기 정보인지? -> error 시 403
        Member member = service.getMember(id);

        return ResponseEntity.ok(member);
    }

    @DeleteMapping
    public ResponseEntity delete(String id){
        // TODO : 로그인 했는지 검사 -> error 시 401
        // TODO : 자기 정보인지? -> error 시 403

        if (service.deleteMember(id)){
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("edit")
    public ResponseEntity edit(@RequestBody Member member){
        // TODO : 로그인 했는지 ? 자기 정보 인지?
        if (service.update(member)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("login")
    public ResponseEntity login(@RequestBody Member member, WebRequest request)  {
        if (service.login(member, request)) {
            return ResponseEntity.ok().build();
        } else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
