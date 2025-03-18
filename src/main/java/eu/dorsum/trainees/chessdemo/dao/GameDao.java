package eu.dorsum.trainees.chessdemo.dao;

import eu.dorsum.trainees.chessdemo.model.Game;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GameDao {

    @Select("select id, white_id, black_id, start_state_id, created, status_name, gameType_name from game")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "whiteId", column = "white_id"),
            @Result(property = "blackId", column = "black_id"),
            @Result(property = "startStateId", column = "start_state_id"),
            @Result(property = "created", column = "created"),
            @Result(property = "statusName", column = "status_name"),
            @Result(property = "gameTypeName", column = "gametype_name")
    })
    List<Game> getGames();

    @Select("select id, white_id, black_id, start_state_id, created, status_name, gameType_name from game where id=#{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "whiteId", column = "white_id"),
            @Result(property = "blackId", column = "black_id"),
            @Result(property = "startStateId", column = "start_state_id"),
            @Result(property = "created", column = "created"),
            @Result(property = "statusName", column = "status_name"),
            @Result(property = "gameTypeName", column = "gametype_name")
    })
    Game getGameById(Long id);

    @Insert("insert into game (id, white_id, black_id, start_state_id, gameType_name) values (seq_game.NEXTVAL, #{whiteId},#{blackId},#{startStateId},#{gameTypeName})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addGame(Game game);
}