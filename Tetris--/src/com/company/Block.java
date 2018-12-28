package com.company;

import java.awt.*;

public enum Block {
    // Blocks should be at most 5 by 5 units
    // OBLOCK cannot be rotated
    OBLOCK  (new int[][] {{0,0}, {1,0}, {0,1}, {1,1}}, Color.YELLOW),
    LBLOCK  (new int[][] {{0,-1}, {0,0}, {0,1}, {1,1}}, Color.ORANGE),
    JBLOCK  (new int[][] {{0,-1}, {0,0}, {0,1}, {-1,1}}, Color.BLUE),
    TBLOCK  (new int[][] {{0,-1}, {0,0}, {-1,0}, {1,0}}, Color.MAGENTA),
    IBLOCK  (new int[][] {{0,-1}, {0,0}, {0,1}, {0,2}}, Color.CYAN),
    SBLOCK  (new int[][] {{-1,0}, {0,0}, {0,-1}, {1,-1}}, Color.GREEN),
    ZBLOCK  (new int[][] {{1,0}, {0,0}, {0,-1}, {-1,-1}}, Color.RED);

    private int[][] points;
    private Color color;

    Block(int[][] points, Color color) {
        this.points = points;
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public int[][] getPoints() {
        return this.points;
    }
}


