import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class PianoTile implements ActionListener, MouseListener{
    
    public final static int COLUMNS = 3, ROWS = 3, TILE_WIDTH = 250, TILE_HEIGHT = 300;

    public static PianoTile dttwt;

    public ArrayList<Tile> tiles;
    public Renderer renderer;
    public Random random;
    public int score, milSecDelay;
    public boolean gameOver;
    public int highScore;

    private final String HIGH_SCORE_FILE = "highscore.txt";

    public PianoTile() {

        JFrame frame = new JFrame("Piano Tiles");
        Timer timer = new Timer(20, this);

        random = new Random();
        renderer = new Renderer();

        frame.setSize(TILE_WIDTH * COLUMNS, TILE_HEIGHT * ROWS);
        frame.add(renderer);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addMouseListener(this);
        frame.setResizable(false);

        loadHighScore();

        Runtime.getRuntime().addShutdownHook(new Thread(this::saveHighScore));

        start();
        timer.start();
    }

    private void loadHighScore() {

        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORE_FILE))) {

            highScore = Integer.parseInt(reader.readLine());

        } catch (IOException | NumberFormatException e) {

            highScore = 0;
        }
    }

    private void saveHighScore() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {

            writer.write(String.valueOf(highScore));
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void start() {

        score = 0;
        gameOver = false;
        tiles = new ArrayList<Tile>();

        for (int x=0 ; x<COLUMNS ; x++) {

            for (int y=0 ; y<ROWS ; y++) {

                boolean canBeBlack = true;

                for (Tile tile : tiles) {

                    if (tile.y == y && tile.black) {

                        canBeBlack = false;
                    }
                }
                if (!canBeBlack) {

                    tiles.add(new Tile(x,y,false));
                } else {

                    tiles.add(new Tile(x,y, random.nextInt(3) == 0 || x == 2)); // 3 columns, 1/3 chance of being black
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        renderer.repaint();

        for (int i=0; i<tiles.size(); i++) {

            Tile tile = tiles.get(i);

            if (tile.animateY < 0) {

                tile.animateY += TILE_HEIGHT/5;
            }
        }

        milSecDelay++;
    }

    public void render(Graphics g) {

        g.setColor(Color.WHITE);
        g.fillRect(0,0, TILE_WIDTH * COLUMNS, TILE_HEIGHT * ROWS);

        g.setFont(new Font("Arial", 1, 100));

        if (!gameOver) {

            for (Tile tile : tiles) {

                g.setColor(tile.black ? Color.BLACK : Color.WHITE);
                g.fillRect(tile.x * TILE_WIDTH, tile.y * TILE_HEIGHT - tile.animateY, TILE_WIDTH, TILE_HEIGHT);
                g.setColor(tile.black ? Color.WHITE : Color.BLACK);
                g.drawRect(tile.x * TILE_WIDTH, tile.y * TILE_HEIGHT - tile.animateY, TILE_WIDTH, TILE_HEIGHT);
                
            }

            g.setColor(Color.RED);
            g.drawString(String.valueOf(score), TILE_WIDTH, 100);
        } else {

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", 1, 50));
            g.drawString("Click to retry", 100, TILE_HEIGHT*2);
            g.setFont(new Font("Arial", 1, 100));
            g.drawString("Game Over!", 100, TILE_HEIGHT);

            g.setFont(new Font("Arial", 1, 40));
            g.drawString("High Score: " + highScore, 100, TILE_HEIGHT * 2 + 100);
        }
    }
    public static void main(String[] args) {
        dttwt = new PianoTile();
    }   

    @Override
    public void mousePressed(MouseEvent e) {

        boolean clicked = false;

        if (!gameOver) {
            for (int i=0; i<tiles.size(); i++) {

                Tile tile = tiles.get(i);

                if (tile.inTile(e.getX(), e.getY()) && !clicked) {

                    if(e.getY() > TILE_HEIGHT * (ROWS - 1)) {

                        if (tile.black) {
                            
                            //      System.out.println("click");
                            for(int j=0; j < tiles.size(); j++) {
                    
                                if (tiles.get(j).y == ROWS) {

                                    tiles.remove(j);
                                } 

                                    tile.y++;
                                    tile.animateY -= TILE_HEIGHT;
                                    
                            }

                            score += Math.max(100 - milSecDelay, 10);
                            if (score > highScore) {
                                highScore = score;
                            }
                                    //      System.out.println("scored " + score);
                            milSecDelay = 0;
                            boolean canBeBlack = true;

                            for (int x=0; x < COLUMNS; x++) {

                                boolean black = random.nextInt(2) == 0 || x == COLUMNS - 1; // 3 columns, 1/3 chance of being black

                                Tile newTile = null;

                                if(canBeBlack && black) {

                                    newTile = new Tile(x, 0, true);
                                    canBeBlack = false;
                                } else {

                                    newTile = new Tile(x, 0, false);
                                }

                                newTile.animateY -= TILE_HEIGHT;    // animate the new tile to fall down

                                tiles.add(newTile);

                            }
                            
                        } else {
                            gameOver = true;
                        }

                        clicked = true;
                    } else {
                        gameOver = true;
                    }
                }
            }
        } else { start(); }
    }

    @Override
    public void mouseClicked(MouseEvent e) {} // Not used

    @Override
    public void mouseReleased(MouseEvent e) {} // Not used

    @Override
    public void mouseEntered(MouseEvent e) {} // Not used

    @Override
    public void mouseExited(MouseEvent e) {} // Not used
    
}
