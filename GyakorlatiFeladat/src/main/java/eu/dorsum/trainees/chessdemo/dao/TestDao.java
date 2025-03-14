package eu.dorsum.trainees.chessdemo.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TestDao {

    @Select("SELECT 1 FROM DUAL")
    int getOne();
}
