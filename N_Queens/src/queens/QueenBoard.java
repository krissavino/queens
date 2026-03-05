package queens;

import javax.swing.*;
import java.awt.*;
import java.awt.RenderingHints;

public class QueenBoard extends JPanel {
    private int[][] queens;
    private static final int BOARD_SIZE = 8;
    private static final int CELL_SIZE = 60;
    private Color queenColor = new Color(128, 0, 128);
    
    public QueenBoard(int[][] queensPositions) {
        setPreferredSize(new Dimension(BOARD_SIZE * CELL_SIZE, BOARD_SIZE * CELL_SIZE));
        this.queens = queensPositions;
    }
    
    public void updateBoard(int[][] newPositions) {
        this.queens = newPositions;
        repaint();
    }
    
    public void setQueenColor(Color color) {
        this.queenColor = color;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;
                
                if ((row + col) % 2 == 0) {
                    g2d.setColor(new Color(240, 217, 181));
                } else {
                    g2d.setColor(new Color(181, 136, 99));
                }
                g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
        
        if (queens != null) {
            ImageIcon queenIcon = new ImageIcon(getClass().getResource("/queen.png"));
            if (queenIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image queenImage = queenIcon.getImage();
                
                for (int i = 0; i < BOARD_SIZE; i++) {
                    int queenRow = BOARD_SIZE - queens[i][0];
                    int queenCol = queens[i][1] - 1;
                    
                    if (queenRow >= 0 && queenRow < BOARD_SIZE && queenCol >= 0 && queenCol < BOARD_SIZE) {
                        int x = queenCol * CELL_SIZE;
                        int y = queenRow * CELL_SIZE;
                        
                        Composite originalComposite = g2d.getComposite();
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                        g2d.drawImage(queenImage, x, y, CELL_SIZE, CELL_SIZE, this);
                        g2d.setComposite(originalComposite);
                    }
                }
            } else {
                g2d.setColor(queenColor);
                g2d.setFont(new Font("Arial", Font.BOLD, 48));
                
                for (int i = 0; i < BOARD_SIZE; i++) {
                    if (queens[i][0] == 0) continue;
                    
                    int queenRow = BOARD_SIZE - queens[i][0];
                    int queenCol = queens[i][1] - 1;
                    
                    if (queenRow >= 0 && queenRow < BOARD_SIZE && queenCol >= 0 && queenCol < BOARD_SIZE) {
                        int x = queenCol * CELL_SIZE + CELL_SIZE / 3;
                        int y = queenRow * CELL_SIZE + CELL_SIZE * 2 / 3;
                        
                        Composite originalComposite = g2d.getComposite();
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                        g2d.drawString("♕", x, y);
                        g2d.setComposite(originalComposite);
                    }
                }
            }
        }
        
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        
        for (int col = 0; col < BOARD_SIZE; col++) {
            char letter = (char) ('a' + col);
            g2d.drawString(String.valueOf(letter), col * CELL_SIZE + CELL_SIZE / 2 - 3, BOARD_SIZE * CELL_SIZE - 5);
        }
        
        for (int row = 0; row < BOARD_SIZE; row++) {
            int number = BOARD_SIZE - row;
            g2d.drawString(String.valueOf(number), 5, row * CELL_SIZE + CELL_SIZE / 2 + 5);
        }
    }
}