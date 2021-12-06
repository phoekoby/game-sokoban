package ru.vsu.gproup7.sokoban.UI;


import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import ru.vsu.gproup7.sokoban.models.CellType;
import ru.vsu.gproup7.sokoban.models.Game;
import ru.vsu.gproup7.sokoban.models.GameState;
import ru.vsu.gproup7.sokoban.models.Maze;
import ru.vsu.gproup7.sokoban.service.GameService;
import ru.vsu.gproup7.sokoban.service.MazeService;
import ru.vsu.gproup7.sokoban.util.JTableUtils;
import ru.vsu.gproup7.sokoban.util.SwingUtils;



import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Objects;


public class WindowGame extends JFrame {

    private int WIDTH_FOR_TABLE;
    private int HEIGHT_FOR_TABLE;
    private final Game sokoban;
    private final GameService gameService;
    private MazeService mazeService;
    private JPanel panel1;
    private JTable gameTable;
    private JButton buttonbeginGame;
    private JLabel stateOfGame;
    private JPanel contentPane;
    private JLabel labelTime;
    private JPanel buttons;
    private JPanel panelTime;
    private JComboBox<Integer> listOfLevels;
    private final Timer timer;
    private static final int DEFAULT_CELL_SIZE = 50;
    private int timeOfBegin = 0;


    public WindowGame(Game game, GameService gameService, MazeService mazeService) {
        this.$$$setupUI$$$();
        this.gameService = gameService;
        this.sokoban = game;
        this.mazeService = mazeService;

        sokoban.setLevelNumber(0);
        sokoban.setCurrentMaze(mazeService.getMazes(sokoban.getFiles().get(sokoban.getLevelNumber())));
        sokoban.setPlayer(sokoban.getCurrentMaze().getGamer());
        gameTable.setRowHeight(DEFAULT_CELL_SIZE);
        recalculation();

        this.setTitle("Сокобан");
        this.setContentPane(panel1);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setPreferredSize(new Dimension(WIDTH_FOR_TABLE + 250, HEIGHT_FOR_TABLE + 100));
        this.setResizable(false);
        this.pack();

        labelTime.setFont(new Font("Times New Roman", Font.PLAIN, 45));
        labelTime.setForeground(new Color(0, 0, 128));

        listOfLevels.addItem(1);
        listOfLevels.addItem(2);
        listOfLevels.addItem(3);


        JTableUtils.initJTableForArray(gameTable, DEFAULT_CELL_SIZE, false, false, false, false);

        JTableUtils.writeArrayToJTable(gameTable, mazeService.fromCellTypeToInt(mazeService.getMazes(sokoban.getFiles().get(sokoban.getLevelNumber())).getMap()));
        gameTable.setPreferredScrollableViewportSize(new Dimension(WIDTH_FOR_TABLE, HEIGHT_FOR_TABLE));
        gameTable.setFillsViewportHeight(true);
        SwingUtils.setShowMessageDefaultErrorHandler();

        gameTable.setIntercellSpacing(new Dimension(0, 0));
        gameTable.setEnabled(false);

        gameTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            final class DrawComponent extends Component {
                private int row = 0, column = 0;

                @Override
                public void paint(Graphics gr) {
                    Graphics2D g2d = (Graphics2D) gr;
                    try {
                        paintCell(row, column, g2d);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            final DrawComponent comp = new DrawComponent();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                comp.row = row;
                comp.column = column;
                return comp;
            }
        });
        timer = new Timer(1000, e -> {
            if (sokoban.getGameState() == GameState.PLAYING) {
                timeOfBegin++;
                labelTime.setText("" + timeOfBegin);
            }
        });
        newGame();
        updateView();
    }


    private void updateView() {
        gameTable.repaint();
    }

    private Font font = null;

    private Font getFont(int size) {
        if (font == null || font.getSize() != size) {
            font = new Font("Comic Sans MS", Font.BOLD, size);
        }
        return font;
    }

    private static final BufferedImage[] images = new BufferedImage[9];

    private static BufferedImage wallImage;
    private static BufferedImage blockImage;
    private static BufferedImage emptyQwadro;
    private static BufferedImage rightPosition;
    private static BufferedImage blockInRightPosition;
    private static BufferedImage gamerImage;

    public BufferedImage getImage(String str) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + str)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return image;
    }

    {
//            System.out.println(this.getClass().getResource("/images/box.png"));
//            String string = Objects.requireNonNull(this.getClass().getResource("/images/box.png")).getPath();
//            string = string.substring(string.indexOf("/classes"));
//            System.out.println(string);
//            System.out.println(this.getClass().getResource("images/box.png"));
            blockImage = getImage("box.png");
            emptyQwadro = getImage("blank.png");
            rightPosition = getImage("goal.png");
            blockInRightPosition = getImage("box-in-goal.png");
            wallImage = getImage("wall.png");
            gamerImage = getImage("player.png");


//            blockImage = ImageIO.read(new File("src/main/resources/images/box.png"));
//            emptyQwadro = ImageIO.read(new File("src/main/resources/images/blank.png"));
//            rightPosition = ImageIO.read(new File("src/main/resources/images/goal.png"));
//            blockInRightPosition = ImageIO.read(new File("src/main/resources/images/box-in-goal.png"));
//            wallImage = ImageIO.read(new File("src/main/resources/images/wall.png"));
//            gamerImage = ImageIO.read(new File("src/main/resources/images/player.png"));

            images[CellType.WALL.getInstance()] = wallImage;
            images[CellType.BLANK.getInstance()] = emptyQwadro;
            images[CellType.BOX.getInstance()] = blockImage;
            images[CellType.BOX_IN_GOAL.getInstance()] = blockInRightPosition;

    }

    private void paintCell(int row, int column, Graphics2D g2d) throws ParseException {
        int[][] cells = gameService.calcStateCurrentMaze(sokoban, mazeService);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int size = DEFAULT_CELL_SIZE;
        assert cells != null;
        int cellValue = cells[row][column];

        if (cellValue == CellType.WALL.getInstance()) {
            g2d.drawImage(wallImage, 0, 0, size, size, null);
            return;
        }
        if (cellValue == CellType.BOX.getInstance()) {
            g2d.drawImage(blockImage, 0, 0, size, size, null);
            return;
        }
        if (cellValue == CellType.BLANK.getInstance()) {
            g2d.drawImage(emptyQwadro, 0, 0, size, size, null);
            return;
        }
        if (cellValue == CellType.GOAL.getInstance()) {
            g2d.drawImage(rightPosition, 0, 0, size, size, null);
            return;
        }
        if (cellValue == CellType.BOX_IN_GOAL.getInstance()) {
            g2d.drawImage(blockInRightPosition, 0, 0, size, size, null);
            return;
        }

        g2d.drawImage(emptyQwadro, 0, 0, size, size, null);
        CellType[][] map = sokoban.getCurrentMaze().getMap();
        if (map[row][column] == CellType.GOAL) {
            g2d.drawImage(rightPosition, 0, 0, size, size, null);
        }
        if (cellValue == CellType.GAMER.getInstance()) {
            g2d.drawImage(gamerImage, 0, 0, size, size, null);
        }
    }


    private void newGame() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    if (e.getID() == KeyEvent.KEY_RELEASED) {
                        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                            gameService.clickLeftBottom(sokoban, sokoban.getLevelNumber());
                            JTableUtils.writeArrayToJTable(gameTable, gameService.calcStateCurrentMaze(sokoban,mazeService));
                            getStateOfGame();
                        }
                        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                            gameService.clickRightBottom(sokoban, sokoban.getLevelNumber());
                            JTableUtils.writeArrayToJTable(gameTable, gameService.calcStateCurrentMaze(sokoban,mazeService));
                            getStateOfGame();
                        }
                        if (e.getKeyCode() == KeyEvent.VK_UP) {
                            gameService.clickUpBottom(sokoban, sokoban.getLevelNumber());
                            JTableUtils.writeArrayToJTable(gameTable, gameService.calcStateCurrentMaze(sokoban,mazeService));
                            getStateOfGame();
                        }
                        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                            gameService.clickDownBottom(sokoban, sokoban.getLevelNumber());
                            JTableUtils.writeArrayToJTable(gameTable, gameService.calcStateCurrentMaze(sokoban,mazeService));
                            getStateOfGame();
                        }
                    }
                    return false;
                });

        listOfLevels.addActionListener(e -> {
            Integer integer = (Integer) listOfLevels.getSelectedItem();
            sokoban.setLevelNumber(integer - 1);
            recalculation();
            sokoban.setGameState(GameState.NOT_STARTED);
            sokoban.setCurrentMaze(mazeService.getMazes(sokoban.getFiles().get(sokoban.getLevelNumber())));
            System.out.println(sokoban.getCurrentMaze());
            JTableUtils.writeArrayToJTable(gameTable, gameService.calcStateCurrentMaze(sokoban, mazeService));
            gameTable.setPreferredScrollableViewportSize(new Dimension(WIDTH_FOR_TABLE, HEIGHT_FOR_TABLE));
            gameTable.setFillsViewportHeight(true);
            contentPane.setPreferredSize(new Dimension(WIDTH_FOR_TABLE, HEIGHT_FOR_TABLE));
            timeOfBegin = 0;
            labelTime.setText("0");

        });

        buttonbeginGame.addActionListener(actionEvent -> {
            try {
                if(GameState.PLAYING != sokoban.getGameState()) {
                    sokoban.setGameState(GameState.PLAYING);
                    stateOfGame.setText("Состояние игры: В процессе...");
                    timeOfBegin = 0;
                    labelTime.setText("0");
                    timer.start();
                }
            } catch (Exception e) {
                SwingUtils.showErrorMessageBox(e);
            }
        });
    }

    public void getStateOfGame() {
        updateView();
        gameService.playGame(sokoban, sokoban.getLevelNumber());
        if (sokoban.getGameState() == GameState.WIN) {
            stateOfGame.setText("Состояние игры: WIN!!!!!!!");
            timer.stop();
            timeOfBegin = 0;
        }
    }

    private void recalculation() {
        WIDTH_FOR_TABLE = DEFAULT_CELL_SIZE * sokoban.getMazes().get(sokoban.getLevelNumber()).getMap()[0].length;
        HEIGHT_FOR_TABLE = DEFAULT_CELL_SIZE * sokoban.getMazes().get(sokoban.getLevelNumber()).getMap().length;
        this.setPreferredSize(new Dimension(WIDTH_FOR_TABLE + 250, HEIGHT_FOR_TABLE + 100));
        this.pack();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        buttons = new JPanel();
        buttons.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        buttons.setEnabled(true);
        panel1.add(buttons, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(200, -1), 0, false));
        buttonbeginGame = new JButton();
        buttonbeginGame.setText("Начать игру");
        buttons.add(buttonbeginGame, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, -1), null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        buttons.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        listOfLevels = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        listOfLevels.setModel(defaultComboBoxModel1);
        listOfLevels.setToolTipText("");
        buttons.add(listOfLevels, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Выберите уровень");
        buttons.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stateOfGame = new JLabel();
        stateOfGame.setText("Состояние игры:");
        panel1.add(stateOfGame, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 50), null, new Dimension(-1, 50), 0, false));
        panelTime = new JPanel();
        panelTime.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panelTime, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_SOUTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 50), new Dimension(100, -1), new Dimension(100, 50), 0, false));
        labelTime = new JLabel();
        labelTime.setText("0");
        panelTime.add(labelTime, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(contentPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gameTable = new JTable();
        gameTable.setFillsViewportHeight(true);
        gameTable.setPreferredScrollableViewportSize(new Dimension(240, 270));
        gameTable.setSelectionBackground(new Color(-14256705));
        contentPane.add(gameTable, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}


//setFocusable
//drawImage


// startTime  ---  long
