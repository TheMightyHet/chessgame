package eu.dorsum.trainees.chessdemo.dao;

import eu.dorsum.trainees.chessdemo.model.Move;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MoveDao {

    @Select("select id, game_id, player_id, notation, elapsed_time_ms from moves where game_id=#{id} order by id")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "gameId", column = "game_id"),
            @Result(property = "playerId", column = "player_id"),
            @Result(property = "notation", column = "notation"),
            @Result(property = "elapsedTimeMs", column = "elapsed_time_ms")
    })
    List<Move> getMovesOfGameAsc(Long id);

    @Insert("insert into moves (game_id, player_id, notation, elapsed_time_ms) values (#{gameId}, #{playerId}, #{notation}, #{elapsedTimeMs})")
    void addMove(Move move);
}
