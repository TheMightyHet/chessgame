package eu.dorsum.trainees.chessdemo.model.enums;

public enum GameType {
    GAMETYPE_90_30("90+30"),
    GAMETYPE_120_30("120+30"),
    GAMETYPE_15_10("15+10"),
    GAMETYPE_25_0("25+0"),
    GAMETYPE_5_0("5+0"),
    GAMETYPE_3_2("3+2");

    private String value;

    GameType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
