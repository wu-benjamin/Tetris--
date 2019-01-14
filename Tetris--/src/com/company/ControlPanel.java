// Project by Benjamin Wu and Alan Zhu for SE101
// Completed/Last Updated 21 November 2018

package com.company;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.URL;
import java.util.*;

public class ControlPanel extends JPanel implements Runnable {

    final static int ROW = 24;
    final static int COL = 12;
    private static boolean run = false;
    private static Random rand = new Random();
    private static int score = 0;
    private static JFrame frame = new JFrame("TETRIS--");
    static int FRAME_RATE = 30;
    private static Board b;
    protected static Input input = new Input();
    private static Player p;
    private static File fontFile;
    private static int highScore = 0;
    private static int gameState = 0; // 0 is start, 1 is dead, 2 is playing

    public ControlPanel() {
        try {
            loadSave();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        loadFont();
        int blockType = rand.nextInt(Block.values().length);
        this.p = new Player( (12 + 8) / 2 - 1, 4, Block.values()[blockType], this);
        this.b = new Board(this, this.p);
        this.addKeyListener(input);
    }

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int newState) {
        gameState = newState;
    }

    public int getHighScore() {
        return highScore;
    }

    public static void loadFont() {
        try {
            URL resource = Board.class.getResource("/Resources/TETRIS.TTF");
            fontFile = new File(resource.toURI());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static File getFontFile() {
        return fontFile;
    }

    public void start() {
        Thread thread = new Thread(this);
        run = true;
        thread.start();
    }

    public static void incrementScore(int increase) {
        score = Math.min(score + increase, 9999999);
    }

    public int getScore() {
        return score;
    }

    public static void lose() {
        //System.out.println("Game over! Your final score is: " + score + "!");
        gameState = 1;
        try {
            save();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try {
            URL resource = Board.class.getResource("/Resources/Sad.wav");
            AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(new File(resource.toURI()));
            Clip music = javax.sound.sampled.AudioSystem.getClip();
            music.open(audioIn);
            music.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        p.setDropRate(Board.DROP_RATE);
    }

    private static void save() throws IOException {
        FileWriter out = null;
        try {
            out = new FileWriter("SaveData.txt");
            if (score > highScore) {
                highScore = score;
            }
            out.write(String.valueOf(highScore));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private static void loadSave() throws IOException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(((new FileReader(("SaveData.txt")))));
            highScore = Integer.parseInt(in.readLine());
        } catch (FileNotFoundException e) {
            highScore = 0;
            save();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public void run() {
        while (run) {
            // Draws game board each frame
            try {
                repaint();b.update(this);
                Thread.sleep(1000 / FRAME_RATE);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        b.paintComponent(g2);
    }


    public static void main(String[] args) {
        try {
            URL resource = Board.class.getResource("/Resources/TetrisMusic.wav");
            AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(new File(resource.toURI()));
            Clip music = javax.sound.sampled.AudioSystem.getClip();
            music.open(audioIn);
            music.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
        ControlPanel control = new ControlPanel();
        frame.setVisible(true);
        frame.setSize(COL * 25 + 300, ROW * 25);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        control.setPreferredSize(new Dimension(COL * 25 + 300, ROW * 25));
        control.setFocusable(true);
        control.setDoubleBuffered(true);
        frame.setBackground(Color.BLACK);
        frame.add(control);
        frame.pack();
        control.start();
        frame.addKeyListener(input);
    }
}

class Input implements KeyListener {
    private boolean[] keys = new boolean[256];

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    public void keyTyped(KeyEvent e) {
        /*
        keys[e.getKeyCode()] = true;
        for (int i = 0; i < keys.length - 1; i++) {
            keys[i] = true;
        }
        */
    }

    public boolean keyUsed() {
        for (int i = 0; i < keys.length - 1; i++) {
            if (keys[i]) {
                return true;
            }
        }
        return false;
    }

    public boolean isKeyDown(int keyCode) {
        return keys[keyCode];
    }

    /*public void clear() {
        for (int i = 0; i < keys.length - 1; i++) {
            keys[i] = false;
        }
    }*/
}
