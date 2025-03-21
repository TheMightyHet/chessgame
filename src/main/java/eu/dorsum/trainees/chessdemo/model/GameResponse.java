package eu.dorsum.trainees.chessdemo.model;


import java.util.Map;

public class GameResponse {
    Game game;
    Map<String,Object> board;
    boolean whiteCheck;
    boolean blackCheck;
    boolean whiteTurn;
    Map<String, Long> history;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Map<String,Object> getBoard() {
        return board;
    }

    public void setBoard(Map<String,Object> board) {
        this.board = board;
    }

    public boolean isWhiteCheck() {
        return whiteCheck;
    }

    public void setWhiteCheck(boolean check) {
        whiteCheck = check;
    }

    public boolean isBlackCheck() {
        return blackCheck;
    }

    public void setBlackCheck(boolean check) {
        blackCheck = check;
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public void setWhiteTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }

    public Map<String, Long> getHistory() {
        return history;
    }

    public void setHistory(Map<String, Long> history) {
        this.history = history;
    }
}
