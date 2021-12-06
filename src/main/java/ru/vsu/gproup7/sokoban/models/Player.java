package ru.vsu.gproup7.sokoban.models;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Player {
    private int rowPosition;
    private int colPosition;

    public Player(int colPosition, int rowPosition) {
        this.rowPosition = rowPosition;
        this.colPosition = colPosition;
    }


}
