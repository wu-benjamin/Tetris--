package com.company;

import javax.naming.ldap.Control;
import java.awt.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Player {
    private static int x;
    private static int y;
    private static int[][] points;
    private static Color color;
    private static int dropRate = 800; // Drop per x milliseconds
    private static Random rand = new Random();
    private static Timer timer = new Timer();
    private static ControlPanel p;
    private static int[][] reservePoints;
    private static Color reserveColor;
    private static Block currentBlock;
    private static Block reserveBlock;

    public Player(int x, int y, Block b, ControlPanel p) {
        this.x = x;
        this.y = y;
        this.points = b.getPoints();
        this.color = b.getColor();
        this.currentBlock = b;
        this.p = p;
        int reserveBlockType = rand.nextInt(Block.values().length);
        this.reservePoints = Block.values()[reserveBlockType].getPoints();
        this.reserveColor = Block.values()[reserveBlockType].getColor();
        this.reserveBlock = Block.values()[reserveBlockType];
        timer();
    }

    public static Color getReserveColor() {
        return reserveColor;
    }

    public static int[][] getReservePoints() {
        return reservePoints;
    }

    public static void swapReserve() {
        Color tempColor = color;
        color = reserveColor;
        reserveColor = tempColor;
        int[][] tempPoints = points;
        points = reservePoints;
        reservePoints = tempPoints;
        Block tempBlock = currentBlock;
        currentBlock = reserveBlock;
        reserveBlock = tempBlock;
        if (Board.playerIntersect(points, x, y)) {
            if (!Board.playerIntersect(points, x + 1, y)) {
                x++;
            } else {
                if (!Board.playerIntersect(points, x - 1, y)) {
                    x--;
                } else {
                    if (!Board.playerIntersect(points, x + 2, y)) {
                        x += 2;
                    } else {
                        if (!Board.playerIntersect(points, x - 2, y)) {
                            x -= 2;
                        } else {
                            swapReserve();
                        }
                    }
                }
            }
        }
    }

    public static void setDropRate(int rate) {
        dropRate = rate;
        timer.cancel();
        timer.purge();
        timer = new Timer();
        DropTask dropTask = new DropTask();
        timer.schedule(dropTask, dropRate, dropRate);
    }

    public static void timer() {
        TimerTask dropTask = new DropTask();
        timer.schedule(dropTask, dropRate, dropRate);
    }

    static class DropTask extends TimerTask {
        @Override
        public void run() {
            if (p.getGameState() == 2) {
                drop();
            }
        }
    }

    public static int getX() {
        return x;
    }

    public static int getY() {
        return y;
    }

    public static int[][] getPoints() {
        return points;
    }

    public static Color getColor() {
        return color;
    }

    public static void rotateClockwise() {
        if (currentBlock != Block.OBLOCK) {
            int[][] newPoints = new int[points.length][points[0].length];
            for (int i = 0; i < points.length; i++) {
                for (int j = 0; j < points[0].length; j++) {
                    newPoints[i][j] = points[i][j];
                }
            }
            for (int i = 0; i < points.length; i++) {
                newPoints[i][0] = (int) Math.round(points[i][0] * Math.cos(Math.PI / 2) - points[i][1] * Math.sin(Math.PI / 2));
                newPoints[i][1] = (int) Math.round(points[i][1] * Math.cos(Math.PI / 2) + points[i][0] * Math.sin(Math.PI / 2));
            }
            if (!Board.playerIntersect(newPoints, x, y)) {
                points = newPoints;
            } else {
                if (!Board.playerIntersect(newPoints, x + 1, y)) {
                    points = newPoints;
                    x++;
                } else {
                    if (!Board.playerIntersect(newPoints, x - 1, y)) {
                        points = newPoints;
                        x--;
                    } else {
                        if (!Board.playerIntersect(newPoints, x + 2, y)) {
                            points = newPoints;
                            x += 2;
                        } else {
                            if (!Board.playerIntersect(newPoints, x - 2, y)) {
                                points = newPoints;
                                x -= 2;
                            } else {
                                rotateCounterClockwise();
                            }
                        }
                    }
                }
            }
        }
    }

    public static void rotateCounterClockwise() {
        if (currentBlock != Block.OBLOCK) {
            int[][] newPoints = new int[points.length][points[0].length];
            for (int i = 0; i < points.length; i++) {
                for (int j = 0; j < points[0].length; j++) {
                    newPoints[i][j] = points[i][j];
                }
            }
            for (int i = 0; i < points.length; i++) {
                newPoints[i][0] = (int) Math.round(points[i][0] * Math.cos(-Math.PI / 2) - points[i][1] * Math.sin(-Math.PI / 2));
                newPoints[i][1] = (int) Math.round(points[i][1] * Math.cos(-Math.PI / 2) + points[i][0] * Math.sin(-Math.PI / 2));
            }
            if (!Board.playerIntersect(newPoints, x, y)) {
                points = newPoints;
            } else {
                if (!Board.playerIntersect(newPoints, x + 1, y)) {
                    points = newPoints;
                    x++;
                } else {
                    if (!Board.playerIntersect(newPoints, x - 1, y)) {
                        points = newPoints;
                        x--;
                    } else {
                        if (!Board.playerIntersect(newPoints, x + 2, y)) {
                            points = newPoints;
                            x += 2;
                        } else {
                            if (!Board.playerIntersect(newPoints, x - 2, y)) {
                                points = newPoints;
                                x -= 2;
                            } else {
                                rotateClockwise();
                            }
                        }
                    }
                }
            }
        }
    }

    public static void moveLeft() {
        int[][] newPoints = new int[points.length][points[0].length];
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[0].length; j++) {
                newPoints[i][j] = points[i][j];
            }
        }        for (int i = 0; i < points.length; i++) {
            newPoints[i][0] = points[i][0] - 1;
        }
        if (!Board.playerIntersect(newPoints, x, y)) {
            x--;
        }
    }

    public static Timer getTimer() {
        return timer;
    }

    public static void moveRight() {
        int[][] newPoints = new int[points.length][points[0].length];
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[0].length; j++) {
                newPoints[i][j] = points[i][j];
            }
        }
        for (int i = 0; i < points.length; i++) {
            newPoints[i][0] = points[i][0] + 1;
        }
        if (!Board.playerIntersect(newPoints, x, y)) {
            x++;
        }
    }

    public static void drop() {
        int[][] newPoints = new int[points.length][points[0].length];
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[0].length; j++) {
                newPoints[i][j] = points[i][j];
            }
        }        for (int i = 0; i < points.length; i++) {
            newPoints[i][1] = points[i][1] + 1;
        }
        if (!Board.playerIntersect(newPoints, x, y)) {
            y++;
        } else {
            Board.add(points, color, x, y);
            int blockType = rand.nextInt(Block.values().length);
            y = 4;
            x = Board.getWidth() / 2 - 1;
            points = reservePoints;
            color = reserveColor;
            currentBlock = reserveBlock;
            reservePoints = Block.values()[blockType].getPoints();
            reserveColor = Block.values()[blockType].getColor();
            reserveBlock = Block.values()[blockType];
            if(Board.playerIntersect(points, x, y)) {
                ControlPanel.lose();
            }
            while (Board.completedRow() != -1) {
                Board.clearRow(Board.completedRow());
            }
        }
    }
}
