package com.example.prj1back.controller;

import com.example.prj1back.domain.Board;
import com.example.prj1back.domain.Member;
import com.example.prj1back.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/board")
public class BoardController {

    private final BoardService service;

    @PostMapping("add")
    // 파일까지 같이 반들려면 @RequestBody로는 안된다
    // 파일을 안 보낼 수도 있으니까 파람에 디폴트 값 부여
    // 여러 파일을 보낼땐 []가 저절로 붙기 때문에 밸류값에 부여
    public ResponseEntity add(Board board,
                              @RequestParam(value = "uploadFiles[]",required = false) MultipartFile[] files,
                              @SessionAttribute(value = "login", required = false) Member login)
                              throws IOException {

        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!service.validate(board)) {
            return ResponseEntity.badRequest().build();
        }


        if (service.save(board, files, login)) {
            ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
        return null;
    }

    // /api/board/list?p=?
    // p = 현재 페이지, default는 기초 페이지
    // /api/board/list?k={keyword}
    @GetMapping("list")
    public Map<String, Object> list(@RequestParam(value = "p", defaultValue = "1") Integer page,
                                    @RequestParam(value = "k", defaultValue = "") String keyword,
                                    @RequestParam(value = "c", defaultValue = "all") String category) {

        return service.list(page, keyword, category);
    }

    @GetMapping("id/{id}")
    public Board get(@PathVariable Integer id) {
        return service.get(id);
    }

    @DeleteMapping("remove/{id}")
    public ResponseEntity remove(@PathVariable Integer id,
                                 @SessionAttribute(value = "login", required = false) Member login) {
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 로그 아웃 후 실행 시 401
        }

        if (!service.hasAccess(id, login)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 따른 아이디로 삭제 할려 했을 때 403
        }

        if (service.remove(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("edit")
    public ResponseEntity edit(Board board,
                               @RequestParam(value = "removeFileIds[]", required = false) List<Integer> removeFileIds,
                               @RequestParam(value = "uploadFiles[]", required = false) MultipartFile[] uploadFiles,
                               @SessionAttribute(value = "login", required = false) Member login) throws IOException {

        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!service.hasAccess(board.getId(), login)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403
        }

        if (service.validate(board)) {
            if (service.update(board, removeFileIds, uploadFiles)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
