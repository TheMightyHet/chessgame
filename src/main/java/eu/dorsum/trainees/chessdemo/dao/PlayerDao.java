package eu.dorsum.trainees.chessdemo.dao;

import eu.dorsum.trainees.chessdemo.model.Player;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PlayerDao {

    @Select("select id, username from player")
    List<Player> getPlayers();

    @Select("select id, username from player where username = #{username}")
    Player getPlayerByUsername(String username);

    @Insert("insert into player (username) values (#{username})")
    void addPlayer(String username);

    @Select("select id, username from player where id = #{id}")
    Player getPlayerById(Long id);

}
