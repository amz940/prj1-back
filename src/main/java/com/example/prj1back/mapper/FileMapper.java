package com.example.prj1back.mapper;

import com.example.prj1back.domain.BoardFile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileMapper {

    @Insert("""
            INSERT INTO boardFile (boardId, name)
            VALUES (#{boardId}, #{name})
            """)
    int insert(Integer boardId, String name);

    @Select("""
            SELECT name, id
            FROM boardFile
            WHERE boardId = #{boardId}
            """)
    List<BoardFile> selectNamesByBoardId(Integer boardId);

}
