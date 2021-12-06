package ru.vsu.gproup7.sokoban.models;

import lombok.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    private List<String> files = new ArrayList<>();
    private List<Maze> mazes;
    private Maze currentMaze;
    private int levelNumber;
    private Player player;
    private GameState gameState;

    {
        files.add("mazes/level_01.txt");
        files.add("mazes/level_02.txt");
        files.add("mazes/level_03.txt");
    }

}
