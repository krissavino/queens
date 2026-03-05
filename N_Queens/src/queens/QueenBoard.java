package queens;

import javax.swing.*;
import java.awt.*;
import javax.swing.ImageIcon;

public class QueenBoard extends JPanel {
    private int[][] queens;
    private static final int BOARD_SIZE = 8;
    private static final int CELL_SIZE = 60;
    
    public QueenBoard(Queen q) {
        setPreferredSize(new Dimension(BOARD_SIZE * CELL_SIZE, BOARD_SIZE * CELL_SIZE));
        extractQueensPosition(q);
    }
    
    private void extractQueensPosition(Queen q) {
        queens = new int[BOARD_SIZE][2];
        Queen current = q;
        int index = BOARD_SIZE - 1;
        
        while (current != null && index >= 0) {
            queens[index][0] = current.getRow();    // rowgf
            queens[index][1] = current.getColumn(); // column
            current = current.getNeighbor();
            index--;
        }
    }
    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Включаем сглаживание для лучшего качества
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Рисуем клетки доски
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
        
        // Загружаем и рисуем ферзей
        java.net.URL imageUrl = getClass().getResource("queen.png");
        if (imageUrl != null) {
            ImageIcon queenIcon = new ImageIcon(imageUrl);
            Image queenImage = queenIcon.getImage();
            
            System.out.println("Размер изображения: " + queenIcon.getIconWidth() + "x" + queenIcon.getIconHeight());
            
            for (int i = 0; i < BOARD_SIZE; i++) {
                int queenRow = BOARD_SIZE - queens[i][0];
                int queenCol = queens[i][1] - 1;
                
                System.out.println("Ферзь: ряд=" + queenRow + ", колонка=" + queenCol);
                
                if (queenRow >= 0 && queenRow < BOARD_SIZE && queenCol >= 0 && queenCol < BOARD_SIZE) {
                    int x = queenCol * CELL_SIZE;
                    int y = queenRow * CELL_SIZE;
                    
                    // Рисуем с явным указанием размеров
                    g2d.drawImage(queenImage, x, y, CELL_SIZE, CELL_SIZE, this);
                    
                    // Для отладки - рисуем красную рамку вокруг места ферзя
                    g2d.setColor(Color.RED);
                    g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                }
            }
        } else {
            System.out.println("Изображение не найдено!");
            g2d.setColor(new Color(128, 0, 128));
            g2d.setFont(new Font("Arial", Font.BOLD, 36));
            for (int i = 0; i < BOARD_SIZE; i++) {
                int queenRow = BOARD_SIZE - queens[i][0];
                int queenCol = queens[i][1] - 1;
                if (queenRow >= 0 && queenRow < BOARD_SIZE && queenCol >= 0 && queenCol < BOARD_SIZE) {
                    g2d.drawString("♕", queenCol * CELL_SIZE + CELL_SIZE/3, 
                                         queenRow * CELL_SIZE + CELL_SIZE*2/3);
                }
            }
        }
        
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