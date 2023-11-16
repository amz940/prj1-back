package com.example.prj1back.service;

import com.example.prj1back.domain.Auth;
import com.example.prj1back.domain.Member;
import com.example.prj1back.mapper.BoardMapper;
import com.example.prj1back.mapper.CommentMapper;
import com.example.prj1back.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper mapper;
    private final CommentMapper commentMapper;
    private final BoardMapper boardMapper;
    private final BoardService boardService;

    public boolean add(Member member) {
        return mapper.insert(member) == 1;
    }

    public String getId(String id) {
        return mapper.selectId(id);
    }

    public String getEmail(String email) {
        return mapper.selectEmail(email);

    }

    public String getNickName(String nickName) {
        return mapper.selectNickName(nickName);
    }

    public boolean validate(Member member) {
        if (member == null) {
            return false;
        }

        if (member.getEmail().isBlank()) {
            return false;
        }

        if (member.getPassword().isBlank()) {
            return false;
        }

        if (member.getId().isBlank()) {
            return false;
        }
        return true;
    }

    public List<Member> list() {
        return mapper.selectAll();
    }

    public Member getMember(String id) {
        return mapper.selectById(id);
    }

    public boolean deleteMember(String id) {
        // 이 멤버가 작성한 댓글 삭제
        commentMapper.deleteByMemberId(id);

        // 1. 이 멤버가 작성한 게시물 삭제
        // 이 멤버가 작성한 게시물 번호들 조회
        List<Integer> boardIdlist = boardMapper.selectIdListByMemberId(id);
        // 게시물 번호들 loop 해서 각 게시물 삭제(boardService.remove)
        boardIdlist.forEach((boardId) -> boardService.remove(boardId));

        boardMapper.deleteByWriter(id);

        // 2. 이 멤버 삭제1
        return mapper.deleteById(id) == 1;
    }

    public boolean update(Member member) {
        Member oldMember = mapper.selectById(member.getId());

//        if (member.getPassword().equals("")) {
//            member.setPassword(oldMember.getPassword());
//        }

        return mapper.update(member) == 1;
    }

    public boolean login(Member member, WebRequest request) {
        Member dbMember = mapper.selectById(member.getId());



        if (dbMember != null){
            if (dbMember.getPassword().equals(member.getPassword())){

                List<Auth> auth = mapper.selectAuthById(member.getId());
                dbMember.setAuth(auth);

                dbMember.setPassword("");
//                쿠키 생성 해주기
                request.setAttribute("login", dbMember, RequestAttributes.SCOPE_SESSION);

                return true;
            }
        }
        return false;
    }

    public boolean hasAccess(String id, Member login) {
        if (isAdmin(login)){
            return true;
        }

        return login.getId().equals(id);
    }
    // admin이 있는 지 확인 해주는 코드
    public boolean isAdmin(Member login){
        if( login.getAuth() != null) {
            return login.getAuth()
                    .stream()
                    .map(Auth::getName)
                    .anyMatch(n -> n.equals("admin"));
        }
        return false;
    }
}