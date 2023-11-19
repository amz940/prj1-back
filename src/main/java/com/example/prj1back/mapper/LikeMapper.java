package com.example.prj1back.mapper;

import com.example.prj1back.domain.Like;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
    Like selectByBoardIdAndMemberId(Integer boardId, String memberId);
}

