package ru.vsu.gproup7.sokoban.models;

public enum CellType {
    WALL(0),
    BLANK(1),
    BOX(4),
    GOAL(6),
    BOX_IN_GOAL(5),
    GAMER(8);

    private final int cellTypeInInt;
    CellType(int i) {
    this.cellTypeInInt=i;
    }
    public int getInstance() {
        return this.cellTypeInInt;
    }
}
