package com.example.prj1back.service;


import com.example.prj1back.domain.Like;
import com.example.prj1back.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeMapper mapper;

    public void update(Like like){
        // 처음 좋아요 누를 때 : insert
        // 다시 누르면 : delete

        int count = 0;

      if ( mapper.delete(like) == 0 ) {
          mapper.insert(like);
      }

    }
}
