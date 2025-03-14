package eu.dorsum.trainees.chessdemo.dao;

import eu.dorsum.trainees.chessdemo.model.StartState;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StartStateDao {
    @Select("select id, sidetomove, positions from start_state")
    List<StartState> getStartState();
}
