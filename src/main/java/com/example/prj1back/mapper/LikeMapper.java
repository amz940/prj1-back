package com.example.prj1back.mapper;

import com.example.prj1back.domain.Like;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LikeMapper {

    @Delete("""
            DELETE FROM boardLike
            WHERE boardId = #{boardId}
            AND memberId = #{memberId}
            """)
    int delete(Like like);

    @Insert("""
            INSERT INTO boardLike (boardId, memberId)
            VALUES (#{boardId}, #{memberId})
            """)
    int insert(Like like);

    @Select("""
            SELECT COUNT(id) FROM boardlike
            WHERE boardId = #{boardId}
            """)
    int countByBoardId(Integer boardId);

    @Select("""
            SELECT * FROM boardlike
            WHERE boardId = #{boardId}
            AND memberId = #{memberId}
            """)
//    Like selectByBoardIdAndMemberId(Integer boardId, String memberId);
    Like selectByBoardIdAndMemberId(@Param("boardId") Integer boardId, @Param("memberId") String memberId);

    @Delete("""
            DELETE FROM boardlike
            WHERE boardId = #{boardId}
            """)
    void deleteByBoardId(Integer id);

    @Delete("""
            DELETE FROM boardlike
            WHERE memberId = #{memberId}
            """)
    int deleteByMemberId(String memberId);

}

