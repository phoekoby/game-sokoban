package ru.vsu.gproup7.sokoban;

import com.intellij.uiDesigner.core.Spacer;
import ru.vsu.gproup7.sokoban.UI.WindowGame;
import ru.vsu.gproup7.sokoban.models.Game;
import ru.vsu.gproup7.sokoban.service.GameService;
import ru.vsu.gproup7.sokoban.service.MazeService;
import ru.vsu.gproup7.sokoban.util.SwingUtils;

public class Sokoban {
    public static void main(String[] args) {

        MazeService mazeService = new MazeService();
        Game game = new Game();
        game.setMazes(mazeService.createMazes());
        GameService gameService = new GameService();



        SwingUtils.setDefaultFont("Microsoft Sans Serif", 18);

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WindowGame(game,gameService,mazeService).setVisible(true);
            }
        });
    }
}
