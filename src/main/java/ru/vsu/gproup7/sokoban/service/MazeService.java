package ru.vsu.gproup7.sokoban.service;

import ru.vsu.gproup7.sokoban.models.CellType;
import ru.vsu.gproup7.sokoban.models.Maze;
import ru.vsu.gproup7.sokoban.models.Player;
import ru.vsu.gproup7.sokoban.util.ArrayUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class MazeService {
    public List<Maze> createMazes() {
        List<Maze> mazes = new ArrayList<>();

        try (ZipInputStream zipInputStream = new ZipInputStream((getClass().getProtectionDomain().getCodeSource().getLocation().openStream()))) {
            while (true) {
                ZipEntry zipEntry = zipInputStream.getNextEntry();
                if (zipEntry == null) {
                    break;
                }
                String name = zipEntry.getName();
                if (name.startsWith("mazes/level")) {
                    int i;
                    int x = 0, y;
                    boolean isX = true;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((i = zipInputStream.read()) != -1) {
                        if (i == 32) {
                            x = Integer.parseInt(stringBuilder.toString());
                            stringBuilder = new StringBuilder();
                            continue;
                        }
                        if (i == '\n' || i == 13) {
                            break;
                        }
                        stringBuilder.append((char) i);
                    }

                    y = Integer.parseInt(stringBuilder.toString());
                    Player gamer = new Player(x, y);
                    List<String> lines = new ArrayList<>();
                    stringBuilder = new StringBuilder();
                    while ((i = zipInputStream.read()) != -1) {
                        if (i == 13 || i == 10) {

                            if (stringBuilder.isEmpty()) {
                                continue;
                            }
                            lines.add(stringBuilder.toString());
                            stringBuilder = new StringBuilder();
                            continue;
                        }
                        stringBuilder.append((char) i);
                    }
                    mazes.add(new Maze(zipEntry.getName(), fromIntArrayToCellTypeArray(toIntArray2(lines.toArray(new String[0]))), gamer));
                }

                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mazes;
    }

    public Maze getMazes(String path) {
        Maze maze = new Maze(null,null,null);
        try (ZipInputStream zipInputStream = new ZipInputStream((getClass().getProtectionDomain().getCodeSource().getLocation().openStream()))) {
            while (true) {
                ZipEntry zipEntry = zipInputStream.getNextEntry();
                if (zipEntry == null) {
                    break;
                }
                String name = zipEntry.getName();
                if (name.startsWith(path)) {
                    int i;
                    int x = 0, y;
                    boolean isX = true;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((i = zipInputStream.read()) != -1) {
                        if (i == 32) {
                            x = Integer.parseInt(stringBuilder.toString());
                            stringBuilder = new StringBuilder();
                            continue;
                        }
                        if (i == '\n' || i == 13) {
                            break;
                        }
                        stringBuilder.append((char) i);
                    }

                    y = Integer.parseInt(stringBuilder.toString());
                    Player gamer = new Player(x, y);
                    List<String> lines = new ArrayList<>();
                    stringBuilder = new StringBuilder();
                    while ((i = zipInputStream.read()) != -1) {
                        if (i == 13 || i == 10) {

                            if (stringBuilder.isEmpty()) {
                                continue;
                            }
                            lines.add(stringBuilder.toString());
                            stringBuilder = new StringBuilder();
                            continue;
                        }
                        stringBuilder.append((char) i);
                    }
                    maze = new Maze(zipEntry.getName(), fromIntArrayToCellTypeArray(toIntArray2(lines.toArray(new String[0]))), gamer);
                }

                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return maze;
    }
//    public List<File> createFileList() {
//        List<File> files = new ArrayList<>();
//        System.out.println(this.getClass().getResource("mazes/level_01.txt"));
//        try (Stream<Path> paths = Files.walk(Paths.get("mazes"))) {
//            paths
//                    .filter(Files::isRegularFile)
//                    .forEach(file -> {
//                        files.add(file.toFile());
//                    });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return files;
//    }

    private int[][] toIntArray2(String[] lines) {
        int[][] arr2 = new int[lines.length][];
        for (int r = 0; r < lines.length; r++) {
            arr2[r] = ArrayUtils.toIntArray(lines[r]);
        }
        return arr2;
    }

    public int[] toInt(CellType[] types) {
        int[] array = new int[types.length];
        for (int i = 0; i < types.length; i++) {
            array[i] = types[i].getInstance();
        }
        return array;
    }

    public CellType[][] fromIntArrayToCellTypeArray(int[][] array) {
        CellType[][] result = new CellType[array.length][];
        for (int i = 0; i < array.length; i++) {
            result[i] = new CellType[array[i].length];
            for (int j = 0; j < array[i].length; j++) {
                result[i][j] = checkType(array[i][j]);
            }
        }
        return result;
    }

    private CellType checkType(int k) {
        return switch (k) {
            case 0 -> CellType.WALL;
            case 1 -> CellType.BLANK;
            case 4 -> CellType.BOX;
            case 5 -> CellType.BOX_IN_GOAL;
            case 6 -> CellType.GOAL;
            case 8 -> CellType.GAMER;
            default -> null;
        };
    }

    public int[][] fromCellTypeToInt(CellType[][] cellTypes) {
        int[][] result = new int[cellTypes.length][];
        for (int i = 0; i < cellTypes.length; i++) {
            result[i] = new int[cellTypes[i].length];
            for (int j = 0; j < cellTypes[i].length; j++) {
                result[i][j] = cellTypes[i][j].getInstance();
            }
        }
        return result;
    }


}
