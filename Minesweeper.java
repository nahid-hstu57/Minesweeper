import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper {

    enum GameState {
        MENU,
        GAME
    }

    GameState state = GameState.MENU;

    JFrame menuFrame;
    JFrame gameFrame;

    int tilesize = 70;
    int numrows;
    int numcols;
    int minecount;

    int boardsize;

    JLabel textLabel = new JLabel();
    JPanel boardPanel = new JPanel();
    JButton restartButton;  

    MineTile[][] mineTiles;
    ArrayList<MineTile> minelist;

    Random random = new Random();

    int tilesClicked = 0;
    boolean gameOver = false;

    private class MineTile extends JButton {
        int r;
        int c;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    public static void main(String[] args) {
        new Minesweeper().showMenu();
    }

    JButton createStyledButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setFocusPainted(false);
        button.setBackground(new Color(50, 50, 50));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(50, 50, 50));
            }
        });

        return button;
    }

    void restartGame() {
        gameFrame.dispose();
        startGame(numrows, numcols, minecount);
    }

    void showMenu() {

        state = GameState.MENU;

        menuFrame = new JFrame("Select Difficulty");
        menuFrame.setSize(400, 500);
        menuFrame.setLocationRelativeTo(null);
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setLayout(new BorderLayout());

        JLabel title = new JLabel("MINESWEEPER");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setForeground(new Color(0, 0, 0));

        menuFrame.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(80, 60, 80, 60));

        JButton easy = createStyledButton("Easy");
        JButton medium = createStyledButton("Medium");
        JButton hard = createStyledButton("Hard");

        easy.addActionListener(e -> startGame(8, 8, 10));
        medium.addActionListener(e -> startGame(8, 8, 20));
        hard.addActionListener(e -> startGame(10, 10, 40));

        buttonPanel.add(easy);
        buttonPanel.add(medium);
        buttonPanel.add(hard);

        menuFrame.add(buttonPanel, BorderLayout.CENTER);

        menuFrame.setVisible(true);
    }

    void startGame(int rows, int cols, int mines) {

        state = GameState.GAME;
        menuFrame.dispose();

        this.numrows = rows;
        this.numcols = cols;
        this.minecount = mines;
        this.boardsize = tilesize * numrows;

        mineTiles = new MineTile[numrows][numcols];
        minelist = new ArrayList<>();
        tilesClicked = 0;
        gameOver = false;

        gameFrame = new JFrame("Minesweeper: " + minecount);
        gameFrame.setSize(boardsize, boardsize + 60);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setResizable(false);
        gameFrame.setLayout(new BorderLayout());
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textLabel = new JLabel("Minesweeper");
        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);

        restartButton = createStyledButton("Restart");

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(textLabel, BorderLayout.CENTER);
        topPanel.add(restartButton, BorderLayout.EAST);

        gameFrame.add(topPanel, BorderLayout.NORTH);

        restartButton.addActionListener(e -> restartGame());

        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(numrows, numcols));
        boardPanel.setBackground(Color.GRAY);
        gameFrame.add(boardPanel);

        for (int r = 0; r < numrows; r++) {
            for (int c = 0; c < numcols; c++) {

                MineTile tile = new MineTile(r, c);
                mineTiles[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.BOLD, 25));

                tile.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {

                        if (gameOver) return;

                        MineTile t = (MineTile) e.getSource();

                        if (e.getButton() == MouseEvent.BUTTON1) {

                            if (t.getText().equals("")) {

                                if (minelist.contains(t)) {
                                    revealMine();
                                } else {
                                    checkMine(t.r, t.c);
                                }
                            }
                        }

                        else if (e.getButton() == MouseEvent.BUTTON3) {

                            if (t.getText().equals("") && t.isEnabled()) {
                                t.setText("🚩");
                            } else if (t.getText().equals("🚩")) {
                                t.setText("");
                            }
                        }
                    }
                });

                boardPanel.add(tile);
            }
        }

        setMines();
        gameFrame.setVisible(true);
    }

    void setMines() {
        int mineleft = minecount;

        while (mineleft > 0) {
            int r = random.nextInt(numrows);
            int c = random.nextInt(numcols);

            MineTile tile = mineTiles[r][c];

            if (!minelist.contains(tile)) {
                minelist.add(tile);
                mineleft--;
            }
        }
    }

    void revealMine() {
        for (MineTile t : minelist) {
            t.setText("💣");
        }
        gameOver = true;
        textLabel.setText("Game Over");
    }

    void checkMine(int r, int c) {

        if (r < 0 || r >= numrows || c < 0 || c >= numcols)
            return;

        MineTile tile = mineTiles[r][c];

        if (!tile.isEnabled())
            return;

        tile.setEnabled(false);
        tilesClicked++;

        int minesfound = 0;

        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                minesfound += countMine(r + i, c + j);

        if (minesfound > 0) {
            tile.setText(String.valueOf(minesfound));
        } else {
            tile.setText("");

            for (int i = -1; i <= 1; i++)
                for (int j = -1; j <= 1; j++)
                    if (!(i == 0 && j == 0))
                        checkMine(r + i, c + j);
        }

        if (tilesClicked == numrows * numcols - minelist.size()) {
            gameOver = true;
            textLabel.setText("You Win!");
        }
    }

    int countMine(int r, int c) {
        if (r < 0 || r >= numrows || c < 0 || c >= numcols)
            return 0;

        return minelist.contains(mineTiles[r][c]) ? 1 : 0;
    }
}
