package eu.dorsum.trainees.chessdemo.model;

public class Moves {
    private Long id;
    private Long game_id;
    private String notation;
    private Long elapsedTimeMs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGame_id() {
        return game_id;
    }

    public void setGame_id(Long game_id) {
        this.game_id = game_id;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public Long getElapsedTimeMs() {
        return elapsedTimeMs;
    }

    public void setElapsedTimeMs(Long elapsedTimeMs) {
        this.elapsedTimeMs = elapsedTimeMs;
    }
}
