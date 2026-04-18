package queens;

import javax.swing.*;
import java.awt.*;
import java.awt.RenderingHints;

public class QueenBoard extends JPanel {
    private int[][] queens;          // массив [N][2]: [i][0] = row (1..N), [i][1] = column (1..N)
    private int boardSize;
    private static final int CELL_SIZE = 60;
    private Color queenColor = new Color(128, 0, 128);
    private boolean showColors = true; // флаг отображения цветов из БД

    public QueenBoard(int[][] queensPositions, int boardSize) {
        this.boardSize = boardSize;
        setPreferredSize(new Dimension(boardSize * CELL_SIZE, boardSize * CELL_SIZE));
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
    
    public void setShowColors(boolean show) {
        this.showColors = show;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Рисуем клетки доски с цветами из базы данных
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;
                
                // Преобразуем координаты для БД (в БД строки 1..boardSize снизу вверх)
                int dbRow = boardSize - row;
                int dbCol = col + 1;
                
                if (showColors) {
                    // Получаем цвет клетки из базы данных
                    QueenState state = DatabaseManager.getState(dbRow, dbCol, boardSize);
                    if (state != null) {
                        switch (state.color) {
                            case "RED":
                                g2d.setColor(new Color(255, 200, 200)); // светло-красный
                                break;
                            case "GREEN":
                                g2d.setColor(new Color(200, 255, 200)); // светло-зелёный
                                break;
                            case "BLUE":
                                g2d.setColor(new Color(200, 200, 255)); // светло-синий
                                break;
                            default:
                                // Классическая шахматная раскраска
                                if ((row + col) % 2 == 0) {
                                    g2d.setColor(new Color(240, 217, 181));
                                } else {
                                    g2d.setColor(new Color(181, 136, 99));
                                }
                                break;
                        }
                    } else {
                        // Если данных нет - используем классическую раскраску
                        if ((row + col) % 2 == 0) {
                            g2d.setColor(new Color(240, 217, 181));
                        } else {
                            g2d.setColor(new Color(181, 136, 99));
                        }
                    }
                } else {
                    // Классическая шахматная раскраска
                    if ((row + col) % 2 == 0) {
                        g2d.setColor(new Color(240, 217, 181));
                    } else {
                        g2d.setColor(new Color(181, 136, 99));
                    }
                }
                
                g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                
                // Рисуем стоимость клетки, если она есть в БД
                if (showColors) {
                    QueenState state = DatabaseManager.getState(dbRow, dbCol, boardSize);
                    if (state != null) {
                        g2d.setColor(Color.BLACK);
                        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                        String costText = String.valueOf(state.cost);
                        FontMetrics fm = g2d.getFontMetrics();
                        int textWidth = fm.stringWidth(costText);
                        int textHeight = fm.getHeight();
                        g2d.drawString(costText, x + CELL_SIZE - textWidth - 2, y + textHeight);
                    }
                }
                
                // Рисуем границы клеток
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }

        // Рисуем ферзей
        if (queens != null) {
            // Попытка загрузить изображение ферзя из ресурсов
            ImageIcon queenIcon = new ImageIcon(getClass().getResource("/queen.png"));
            boolean imageLoaded = (queenIcon.getImageLoadStatus() == MediaTracker.COMPLETE);
            Image queenImage = imageLoaded ? queenIcon.getImage() : null;

            for (int i = 0; i < boardSize; i++) {
                // Если ферзь не установлен (row == 0), пропускаем
                if (queens[i][0] == 0) continue;

                int queenRow = boardSize - queens[i][0]; // преобразование из 1..N в 0..N-1 сверху вниз
                int queenCol = queens[i][1] - 1;

                if (queenRow >= 0 && queenRow < boardSize && queenCol >= 0 && queenCol < boardSize) {
                    int x = queenCol * CELL_SIZE;
                    int y = queenRow * CELL_SIZE;

                    Composite originalComposite = g2d.getComposite();
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));

                    if (imageLoaded) {
                        g2d.drawImage(queenImage, x, y, CELL_SIZE, CELL_SIZE, this);
                    } else {
                        g2d.setColor(queenColor);
                        g2d.setFont(new Font("Arial", Font.BOLD, 48));
                        g2d.drawString("♕", x + CELL_SIZE / 3, y + CELL_SIZE * 2 / 3);
                    }

                    g2d.setComposite(originalComposite);
                }
            }
        }

        // Рисуем координаты (буквы и цифры)
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));

        // Буквы снизу (a, b, c, ...)
        for (int col = 0; col < boardSize; col++) {
            char letter = (char) ('a' + col);
            g2d.drawString(String.valueOf(letter), col * CELL_SIZE + CELL_SIZE / 2 - 3, boardSize * CELL_SIZE - 5);
        }

        // Цифры слева (1..N сверху вниз, но рисуем от N до 1)
        for (int row = 0; row < boardSize; row++) {
            int number = boardSize - row;
            g2d.drawString(String.valueOf(number), 5, row * CELL_SIZE + CELL_SIZE / 2 + 5);
        }
    }
}