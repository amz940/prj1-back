package com.example.prj1back.service;

import com.example.prj1back.domain.Board;
import com.example.prj1back.domain.BoardFile;
import com.example.prj1back.domain.Member;
import com.example.prj1back.mapper.BoardMapper;
import com.example.prj1back.mapper.CommentMapper;
import com.example.prj1back.mapper.FileMapper;
import com.example.prj1back.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class BoardService {

    private final CommentMapper commentMapper;
    private final LikeMapper likeMapper;
    private final BoardMapper mapper;
    private final FileMapper fileMapper;
    private final S3Client s3;

    @Value("${image.file.prefix}")
    private String urlPrefix;
    @Value("${aws.s3.bucket.name}")
    private String bucket;

    public boolean save(Board board, MultipartFile[] files, Member login) throws IOException {
        // 로그인한 사용자의 이름을 작성자로 바꾸는 코드
        board.setWriter(login.getId());

        int cnt = mapper.insert(board);

        // boardFile 테이블에 files 정보 저장 코드
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                // 특정 파일 정보(boardId, name)만 입력
                fileMapper.insert(board.getId(), files[i].getOriginalFilename());
                // local에 저장
                upload(board.getId(), files[i]);
                // 실제 파일을 S3 bucket에 upload 하는 코드
            }
        }
        return cnt == 1;
    }

    private void upload(Integer boardId, MultipartFile file) throws IOException {
        // 파일 저장 경로
        // aws에 저장하는 방법
        String key = "prj14086/" + boardId + "/" + file.getOriginalFilename();
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();


        s3.putObject(objectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));


//         local에 저장하는 방법
//         C: \Temp\prj1\게시물번호\파일명
//            File folder = new File("C:\\Temp\\prj1" + boardId);
//            if (!folder.exists()) {
//                folder.mkdirs();
//            }
//
//            String path = folder.getAbsolutePath() + "\\" + file.getOriginalFilename();
//
//            File des = new File(path);
//
//            file.transferTo(des);
    }


    public boolean validate(Board board) {
        if (board == null) {
            return false;
        }

        if (board.getContent() == null || board.getContent().isBlank()) {
            return false;
        }

        if (board.getTitle() == null || board.getTitle().isBlank()) {
            return false;
        }

        return true;
    }

    public Map<String, Object> list(Integer page, String keyword, String category) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> pageInfo = new HashMap<>();

//        int countAll = mapper.countAll();
        int countAll = mapper.countAll("%" + keyword + "%", category);

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

        map.put("boardList", mapper.selectAll(from, "%" + keyword + "%", category));
        map.put("pageInfo", pageInfo);

        return map;
    }

    public Board get(Integer id) {
        Board board = mapper.selectById(id);

        List<BoardFile> boardFiles = fileMapper.selectNamesByBoardId(id);

        for (BoardFile boardFile : boardFiles) {
            String url = urlPrefix + "prj14086/" + id + "/" + boardFile.getName();
            boardFile.setUrl(url);
        }

        board.setFiles(boardFiles);

        return board;
    }

    public boolean remove(Integer id) {
        // 1. 게시물에 달린 댓글들 지우기
        commentMapper.deleteByBoardId(id);
        // 2. 좋아요 테이블 지우기
        likeMapper.deleteByBoardId(id);
        // 3. aws에서 파일 지우기
        deleteFile(id);
        // 4. 파일 레코드 지우기
        fileMapper.deleteByBoardId(id);

        return mapper.deleteById(id) == 1;
    }

    private void deleteFile(Integer id) {
        // 파일명 조회
        List<BoardFile> boardFiles = fileMapper.selectNamesByBoardId(id);

        // s3 bucket objects 지우기
        for (BoardFile file : boardFiles) {
            String key = "prj14086/" + id + "/" + file.getName();
            DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3.deleteObject(objectRequest);
        }
    }

    public boolean update(Board board, List<Integer> removeFileIds, MultipartFile[] uploadFiles) throws IOException {
        // 파일 지우기
        // s3에서 지우고
        if (removeFileIds != null) {
            for (Integer id : removeFileIds){
                BoardFile file = fileMapper.selectById(id);
                String key = "prj14086" + board.getId() + "/" + file.getName();
                DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();
                s3.deleteObject(objectRequest);

                // db에서도 지우기
                fileMapper.deleteById(id);
            }
        }
        // 파일 추가하기
        if ( uploadFiles != null) {
            // s3에 올리고
            for (MultipartFile file : uploadFiles) {
                upload(board.getId(), file);
                // db에서도 추가
                fileMapper.insert(board.getId(), file.getOriginalFilename());
            }
        }


        return mapper.update(board) == 1;
    }

    public boolean hasAccess(Integer id, Member login) {
        if (login == null) {
            return false;
        }

        if (login.isAdmin()) {
            return true;
        }

        Board board = mapper.selectById(id);

        return board.getWriter().equals(login.getId());
    }

}
