package eu.dorsum.trainees.chessdemo.model.enums;

public enum Status {
    ONGOING("ONGOING"),
    WHITE_WON("WHITE_WON"),
    BLACK_WON("BLACK_WON"),
    DRAW("DRAW");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}