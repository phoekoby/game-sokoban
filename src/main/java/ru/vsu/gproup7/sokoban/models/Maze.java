package ru.vsu.gproup7.sokoban.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
public class Maze {
    private String name;
    private CellType[][] map;
    private Player gamer;
}
