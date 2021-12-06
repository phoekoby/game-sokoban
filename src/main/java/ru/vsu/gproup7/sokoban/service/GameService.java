package ru.vsu.gproup7.sokoban.service;

import ru.vsu.gproup7.sokoban.models.*;

import java.util.Arrays;
import java.util.Collections;

public class GameService {
    public int[][] calcState(Game game, int numberOfMaze, MazeService mazeService) {
        game.setPlayer(game.getMazes().get(numberOfMaze).getGamer());
        Player gamer = game.getPlayer();
        CellType[][] map = game.getMazes().get(numberOfMaze).getMap();
        return toArrayInt(map,gamer,mazeService);
    }
    public int[][] calcStateCurrentMaze(Game game,MazeService mazeService) {
        game.setPlayer(game.getCurrentMaze().getGamer());
        Player gamer = game.getCurrentMaze().getGamer();
        CellType[][] map = game.getCurrentMaze().getMap();
        checkHowManyBoxes(game);
        return toArrayInt(map,gamer,mazeService);
    }
    private int checkHowManyBoxes(Game game){
        int count = 0;
        CellType[][] map = game.getCurrentMaze().getMap();
        for (int i = 0; i < map.length; i++){
            for (int j = 0; j < map[i].length;j++){
                if(map[i][j] == CellType.BOX){
                    count++;
                }
            }
        }
        if(count==0){
            game.setGameState(GameState.WIN);
        }
        return count;
    }
    private int[][] toArrayInt(CellType[][] map, Player gamer, MazeService mazeService){
        int[][] array = new int[map.length][];
        for (int r = 0; r < map.length; r++) {
            array[r] = new int[map[r].length];
            System.arraycopy(mazeService.toInt(map[r]), 0, array[r], 0, map[r].length);
        }
        if (gamer.getRowPosition() >= 0 && gamer.getColPosition() >= 0) {
            array[gamer.getRowPosition()][gamer.getColPosition()] = CellType.GAMER.getInstance();
        }
        return array;
    }

    public void playGame(Game game, int numberOfLevel) {
        CellType[][] map = game.getMazes().get(numberOfLevel).getMap();
        for (CellType[] cellTypes : map) {
            for (CellType cellType : cellTypes) {
                if (cellType == CellType.BOX) {
                    return;
                }
            }
        }
        game.setGameState(GameState.WIN);
    }

    public void clickDownBottom(Game game, int numberOfLevel) {
        if (GameState.PLAYING == game.getGameState()) {
            movementOfGamer(1, 0, game, numberOfLevel);
        }

    }

    public void clickUpBottom(Game game, int numberOfLevel) {
        if (GameState.PLAYING == game.getGameState()) {
            movementOfGamer(-1, 0, game, numberOfLevel);
        }

    }

    public void clickLeftBottom(Game game, int numberOfLevel) {
        if (GameState.PLAYING == game.getGameState()) {
            movementOfGamer(0, -1, game, numberOfLevel);
        }
    }

    public void clickRightBottom(Game game, int numberOfLevel) {
        if (GameState.PLAYING == game.getGameState()) {
            movementOfGamer(0, 1, game, numberOfLevel);
        }
    }

    public void movementOfGamer(int r, int c, Game game, int numberOfLevel) {
        Player gamer = game.getPlayer();
        CellType[][] map = game.getCurrentMaze().getMap();
        if (gamer.getRowPosition() + r > map.length) {
            return;
        }
        if ((map[gamer.getRowPosition() + r][gamer.getColPosition() + c] != CellType.WALL &&
                map[gamer.getRowPosition() + r][gamer.getColPosition() + c] != CellType.BOX &&
                map[gamer.getRowPosition() + r][gamer.getColPosition() + c] != CellType.BOX_IN_GOAL) ||
                ((map[gamer.getRowPosition() + r][gamer.getColPosition() + c] == CellType.BOX ||
                        map[gamer.getRowPosition() + r][gamer.getColPosition() + c] == CellType.BOX_IN_GOAL) &&
                        map[gamer.getRowPosition() + 2 * r][gamer.getColPosition() + 2 * c] != CellType.WALL &&
                        map[gamer.getRowPosition() + 2 * r][gamer.getColPosition() + 2 * c] != CellType.BOX &&
                        map[gamer.getRowPosition() + 2 * r][gamer.getColPosition() + 2 * c] != CellType.BOX_IN_GOAL)) {
            gamer.setRowPosition(gamer.getRowPosition() + r);
            gamer.setColPosition(gamer.getColPosition() + c);
            if (((map[gamer.getRowPosition()][gamer.getColPosition()] == CellType.BOX ||
                    map[gamer.getRowPosition()][gamer.getColPosition()] == CellType.BOX_IN_GOAL) &&
                    map[gamer.getRowPosition() + r][gamer.getColPosition() + c] != CellType.WALL &&
                    map[gamer.getRowPosition() + r][gamer.getColPosition() + c] != CellType.BOX &&
                    map[gamer.getRowPosition() + r][gamer.getColPosition() + c] != CellType.BOX_IN_GOAL)) {
                if (map[gamer.getRowPosition() + r][gamer.getColPosition() + c] == CellType.GOAL) {
                    map[gamer.getRowPosition() + r][gamer.getColPosition() + c] = CellType.BOX_IN_GOAL;
                    if (map[gamer.getRowPosition()][gamer.getColPosition()] == CellType.BOX_IN_GOAL) {
                        map[gamer.getRowPosition()][gamer.getColPosition()] = CellType.GOAL;
                    } else {
                        map[gamer.getRowPosition()][gamer.getColPosition()] = CellType.BLANK;
                    }
                } else if (map[gamer.getRowPosition()][gamer.getColPosition()] == CellType.BOX_IN_GOAL) {
                    map[gamer.getRowPosition() + r][gamer.getColPosition() + c] = CellType.BOX;
                    map[gamer.getRowPosition()][gamer.getColPosition()] = CellType.GOAL;
                } else {
                    map[gamer.getRowPosition() + r][gamer.getColPosition() + c] = CellType.BOX;
                    if (map[gamer.getRowPosition()][gamer.getColPosition()] == CellType.BOX_IN_GOAL) {
                        map[gamer.getRowPosition()][gamer.getColPosition()] = CellType.GOAL;
                    } else {
                        map[gamer.getRowPosition()][gamer.getColPosition()] = CellType.BLANK;
                    }
                }
            }

        }
    }
}
