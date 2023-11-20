package com.example.prj1back.service;

import com.example.prj1back.domain.Board;
import com.example.prj1back.domain.Member;
import com.example.prj1back.mapper.BoardMapper;
import com.example.prj1back.mapper.CommentMapper;
import com.example.prj1back.mapper.FileMapper;
import com.example.prj1back.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final CommentMapper commentMapper;
    private final LikeMapper likeMapper;
    private final BoardMapper mapper;
    private final FileMapper fileMapper;

    public boolean save(Board board, MultipartFile[] files, Member login) {
        // 로그인한 사용자의 이름을 작성자로 바꾸는 코드
        board.setWriter(login.getId());

        int cnt = mapper.insert(board);

        // boardFile 테이블에 files 정보 저장 코드
        if (files != null){
            for (int i = 0; i < files.length; i++) {
                // 특정 파일 정보(boardId, name)만 입력
                fileMapper.insert(board.getId(), files[i].getOriginalFilename());
                // local에 저장
                upload(files[i]);
                // 실제 파일을 S3 bucket에 upload 하는 코드
            }
        }
        return cnt == 1 ;
    }

    private void upload(Integer boardId, MultipartFile file) {
        // 파일 저장 경로
        // C: \Temp\prj1\게시물번호\파일명
        try {
            File folder = new File("C:\\Temp\\prj1" + boardId);
            if( !folder.exists()){
                folder.mkdirs();
            }

            String path = folder.getAbsolutePath() + "\\" + file.getOriginalFilename();

            File des = new File(path);

            file.transferTo(des);


        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public boolean validate(Board board) {
        if ( board == null){
            return false;
        }

        if (board.getContent() == null || board.getContent().isBlank()){
            return false;
        }

        if (board.getTitle() == null || board.getTitle().isBlank()){
            return false;
        }

        return true;
    }

    public Map<String, Object> list(Integer page, String keyword) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> pageInfo = new HashMap<>();

//        int countAll = mapper.countAll();
        int countAll = mapper.countAll("%"+ keyword + "%");

        int lastPageNumber = (countAll - 1) / 10 + 1;
        int startPageNumber = (page - 1) / 10 * 10 + 1;
        int endPageNumber = startPageNumber + 9;

        endPageNumber = Math.min(endPageNumber, lastPageNumber);

        int prevPageNumber = startPageNumber - 10;
        int nextPageNumber = endPageNumber + 1;

        // 처음 페이지
        pageInfo.put("startPageNumber", startPageNumber);
        // 현재 페이지
        pageInfo.put("currentPageNumber", page);
        // 끝 페이지
        pageInfo.put("endPageNumber", endPageNumber);

        if (prevPageNumber > 0) {
            pageInfo.put("prevPageNumber", prevPageNumber);
        }
        if (nextPageNumber <= lastPageNumber) {
            pageInfo.put("nextPageNumber", nextPageNumber);
        }

        int from = (page - 1) * 10;

        map.put("boardList", mapper.selectAll(from, "%" + keyword + "%"));
        map.put("pageInfo", pageInfo);

        return map;
    }

    public Board get(Integer id) {
        return mapper.selectById(id);
    }

    public boolean remove(Integer id) {
        // 1. 게시물에 달린 댓글들 지우기
            commentMapper.deleteByBoardId(id);
        // 2. 좋아요 테이블 지우기
            likeMapper.deleteByBoardId(id);

        return mapper.deleteById(id) == 1 ;
    }

    public boolean update(Board board) {
        return mapper.update(board) == 1;
    }

    public boolean hasAccess(Integer id, Member login){
        if (login == null) {
            return false;
        }

        if (login.isAdmin()){
            return true;
        }

        Board board = mapper.selectById(id);

        return board.getWriter().equals(login.getId());
    }

}
