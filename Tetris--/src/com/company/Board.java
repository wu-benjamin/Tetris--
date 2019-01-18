package com.company;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

class Board {
    private static Color[][] color;
    private static final Color TRANSPARENT = new Color(255,255,255,40);
    private static int length;
    private static int width;
    private static boolean cooldown = false;
    private static Timer timer = new Timer();
    private static Player p;
    private static ControlPanel panel;
    static final int DROP_RATE = 800;
    private static Font font;

    static {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, ControlPanel.getFontFile()).deriveFont(50f);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    Board(ControlPanel controlPanel, Player player) {
        // Buffer
        length = ControlPanel.ROW + 12;
        width = ControlPanel.COL + 8;
        color = new Color[length][width];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                color[i][j] = TRANSPARENT;
            }
        }
        panel = controlPanel;
        p = player;
    }

    private static void reset() {
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                color[i][j] = TRANSPARENT;
            }
        }
    }

    static void add(int[][] points, Color c, int x, int y) {
        for (int i = 0; i < points.length; i++) {
            color[points[i][1] + y][points[i][0] + x] = c;
        }
        try {
            URL resource = Board.class.getResource("/Resources/Ploop.wav");
            AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(new File(resource.toURI()));
            Clip music = javax.sound.sampled.AudioSystem.getClip();
            music.open(audioIn);
            music.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static Color[][] toDisplay() {
        Color[][] toDisplay = new Color[length][width];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                toDisplay[i][j] = color[i][j];
            }
        }
        Color c = Player.getColor();
        for (int i = 0; i < Player.getPoints().length; i++) {
            toDisplay[Player.getPoints()[i][1] + Player.getY()][Player.getPoints()[i][0] + Player.getX()] = c;
        }
        return toDisplay;
    }

    static boolean playerIntersect(int[][] points, int x, int y) {
        boolean intersects = false;
        for (int i = 0; i < points.length; i++) {
            // System.out.print(points.length + "\n" + points[0].length + "\n" + (points[i][0] + x) + "\n" + (points[i][1] + y) + "\n" + x + "\n" + y + "\n" + color.length + "\n" + color[0].length + "\n\n");
            if (color[points[i][1] + y][points[i][0] + x].getRGB() != TRANSPARENT.getRGB()) {
                intersects = true;
                //System.out.println("Collide1");
            }
            if (points[i][0] + x < 4 || points[i][0] + x >= ControlPanel.COL + 4 || points[i][1] + y < 0 || points[i][1] + y >= ControlPanel.ROW + 8) {
                intersects = true;
                //System.out.println("Collide2");
            }
        }
        // System.out.println("____________________________________________________________");
        return intersects;
    }

    // Returns -1 if no completed row, otherwise returns index of completed row
    static int completedRow() {
        int complete;
        for (int i = 0; i < length; i++) {
            complete = i;
            for (int j = 4; j < width - 4; j++) {
                if (color[i][j].getRGB() == TRANSPARENT.getRGB()) {
                    complete = -1;
                }
            }
            if (complete != -1) {
                return complete;
            }
        }
        return -1;
    }

    static void clearRow(int c) {
        for (int i = c; i > 0; i--) {
            for (int j = 0; j < width; j++) {
                color[i][j] = color[i - 1][j];
            }
        }
        for (int i = 0; i < 4; i ++) {
            for (int j = 0; j < width; j++) {
                color[i][j] = TRANSPARENT;
            }
        }
        ControlPanel.incrementScore(500);
        Player.setDropRate(Math.max(DROP_RATE - panel.getScore() / 50, 250));
        try {
            URL resource = Board.class.getResource("/Resources/Destroy.wav");
            AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(new File(resource.toURI()));
            Clip music = javax.sound.sampled.AudioSystem.getClip();
            music.open(audioIn);
            music.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //System.out.println("You have scored " + panel.getScore() + " so far! Keep up the good work!");
    }

    void paintComponent(Graphics2D g) {
        g.setColor(new Color(20, 20, 20));
        g.fill3DRect(0, 0, ControlPanel.COL * 25 + 400, ControlPanel.ROW * 25, false);
        g.setColor(new Color(255,255,255,150));
        g.fill3DRect(0, -1, ControlPanel.COL * 25 + 400, 2, false);
        g.fill3DRect(0, ControlPanel.ROW * 25 - 2, ControlPanel.COL * 25 + 400, 2, false);
        g.fill3DRect(ControlPanel.COL * 25, 74, 400, 2, false);
        g.fill3DRect(ControlPanel.COL * 25, 149, 400, 2, false);
        g.fill3DRect(ControlPanel.COL * 25, 374, 400, 2, false);
        g.fill3DRect(ControlPanel.COL * 25 + 300 - 80, 74, 2, 75, false);
        g.fill3DRect(ControlPanel.COL * 25 + 300 - 1, 0, 2, ControlPanel.ROW * 25, false);
        for (int i = 8; i <= length - 4; i++) {
            g.fill3DRect(0, (i - 8) * 25 - 1, ControlPanel.COL * 25, 2, false);
        }
        for (int i = 4; i <= width - 4; i++) {
            g.fill3DRect((i - 4) * 25 - 1, 0, 2, ControlPanel.ROW * 25, false);
        }
        for (int i = 8; i < length - 4; i++) {
            for (int j = 4; j < width - 4; j++) {
                g.setColor(Board.toDisplay()[i][j]);
                //g.draw3DRect((j - 4) * 25, (i - 8) * 25, 25, 25, true);
                if (Board.toDisplay()[i][j].getRGB() != TRANSPARENT.getRGB()) {
                    g.setColor(new Color(Board.toDisplay()[i][j].getRed(), Board.toDisplay()[i][j].getGreen(), Board.toDisplay()[i][j].getBlue(), 180));
                }
                g.fill3DRect((j - 4) * 25, (i - 8) * 25, 25, 25, true);
            }
        }
        g.setFont(font);
        g.setColor(Color.RED);
        g.drawString("T", 340, 60);
        g.setColor(Color.BLUE);
        g.drawString("E", 380, 60);
        g.setColor(Color.MAGENTA);
        g.drawString("T", 420, 60);
        g.setColor(Color.CYAN);
        g.drawString("R", 460, 60);
        g.setColor(Color.YELLOW);
        g.drawString("I", 500, 60);
        g.setColor(Color.GREEN);
        g.drawString("S", 540, 60);
        g.setColor(new Color(30,150, 50));
        g.drawString("NEXT: ", 340, 140);
        g.setColor(Player.getReserveColor());
        for (int i = 0; i < Player.getReservePoints().length; i++) {
            g.fill3DRect(557 + 11 * Player.getReservePoints()[i][0],
                    108 + 11 * Player.getReservePoints()[i][1], 10, 10, false);
        }
        g.setColor(new Color(218, 165, 32));
        g.drawString("HIGH", 340, 220);
        g.drawString("SCORE", 340, 280);
        g.drawString(String.format("%07d", panel.getHighScore()), 340, 350);
        g.setColor(Color.WHITE);
        g.drawString("CURRENT", 340, 450);
        g.drawString("SCORE", 340, 510);
        g.drawString(String.format("%07d", panel.getScore()), 340, 570);
        if (panel.getGameState() != 2) {
            g.setColor(new Color(10, 10, 10, 200));
            g.fill3DRect(0, 0, ControlPanel.COL * 25 + 400, ControlPanel.ROW * 25, false);
        }
        if (panel.getGameState() == 0) {
            g.setColor(Color.GREEN);
            g.setColor(Color.RED);
            g.drawString("T", ControlPanel.COL * 25 - 105, ControlPanel.ROW * 25 / 2 - 150);
            g.setColor(Color.BLUE);
            g.drawString("E", ControlPanel.COL * 25 - 65, ControlPanel.ROW * 25 / 2 - 150);
            g.setColor(Color.MAGENTA);
            g.drawString("T", ControlPanel.COL * 25 - 25, ControlPanel.ROW * 25 / 2 - 150);
            g.setColor(Color.CYAN);
            g.drawString("R", ControlPanel.COL * 25 + 15, ControlPanel.ROW * 25 / 2 - 150);
            g.setColor(Color.YELLOW);
            g.drawString("I", ControlPanel.COL * 25 + 55, ControlPanel.ROW * 25 / 2 - 150);
            g.setColor(Color.GREEN);
            g.drawString("S", ControlPanel.COL * 25 + 95, ControlPanel.ROW * 25 / 2 - 150);
            g.drawString("PRESS SPACE", ControlPanel.COL * 25 - 180, ControlPanel.ROW * 25 / 2);
            g.drawString("TO PLAY", ControlPanel.COL * 25 - 115, ControlPanel.ROW * 25 / 2 + 150);
        } else if (panel.getGameState() == 1) {
            g.setColor(Color.RED);
            g.drawString("GAME OVER!", ControlPanel.COL * 25 - 160, ControlPanel.ROW * 25 / 2 - 180);
            g.setColor(new Color(218, 165, 32));
            g.drawString("YOU SCORED ", ControlPanel.COL * 25 - 155, ControlPanel.ROW * 25 / 2 - 80);
            g.drawString(panel.getScore() + " POINTS!", ControlPanel.COL * 25 - 180, ControlPanel.ROW * 25 / 2);
            g.setColor(Color.GREEN);
            g.drawString("PRESS SPACE", ControlPanel.COL * 25 - 180, ControlPanel.ROW * 25 / 2 + 90);
            g.drawString("TO PLAY AGAIN", ControlPanel.COL * 25 - 195, ControlPanel.ROW * 25 / 2 + 180);
        }
    }

    static int getWidth() {
        return width;
    }

    void update(ControlPanel panel) {
        if (ControlPanel.input.keyUsed() && !cooldown) {
            if (ControlPanel.input.isKeyDown(KeyEvent.VK_A)) {
                Player.moveLeft();
                cooldown = true;
                TimerTask cooldown = new CooldownTask();
                timer.schedule(cooldown, 100);
            } else if (ControlPanel.input.isKeyDown(KeyEvent.VK_D)) {
                Player.moveRight();
                cooldown = true;
                TimerTask cooldown = new CooldownTask();
                timer.schedule(cooldown, 100);
            } else if (ControlPanel.input.isKeyDown(KeyEvent.VK_LEFT)) {
                Player.rotateCounterClockwise();
                cooldown = true;
                TimerTask cooldown = new CooldownTask();
                timer.schedule(cooldown, 325);
            } else if (ControlPanel.input.isKeyDown(KeyEvent.VK_RIGHT)) {
                Player.rotateClockwise();
                cooldown = true;
                TimerTask cooldown = new CooldownTask();
                timer.schedule(cooldown, 325);
            } else if (ControlPanel.input.isKeyDown(KeyEvent.VK_DOWN) && panel.getGameState() == 2) {
                Player.drop();
                cooldown = true;
                TimerTask cooldown = new CooldownTask();
                timer.schedule(cooldown, 100);
            } else if (ControlPanel.input.isKeyDown(KeyEvent.VK_SPACE) && panel.getGameState() != 2) {
                panel.setGameState(2);
                try {
                    URL resource = Board.class.getResource("/Resources/Start.wav");
                    AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(new File(resource.toURI()));
                    Clip music = javax.sound.sampled.AudioSystem.getClip();
                    music.open(audioIn);
                    music.start();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                ControlPanel.incrementScore(panel.getScore() * (-1));
                reset();
                cooldown = true;
                TimerTask cooldown = new CooldownTask();
                timer.schedule(cooldown, 250);
            } else if (ControlPanel.input.isKeyDown(KeyEvent.VK_SPACE) && panel.getGameState() == 2) {
                Player.swapReserve();
                cooldown = true;
                TimerTask cooldown = new CooldownTask();
                timer.schedule(cooldown, 250);
            }
        }
    }

    class CooldownTask extends TimerTask {
        @Override
        public void run() {
            cooldown = false;
        }
    }
}
