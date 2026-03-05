package queens;

import javax.swing.*;

public class QueensSolver {
    
    public static void main(String[] args) {
        // Решение задачи о 8 ферзях
        Queen lastQueen = null;
        
        for (int i = 0; i < 8; i++) {
            int column = i + 1;
            lastQueen = new Queen(column, lastQueen);
            
            if (!lastQueen.findSolution()) {
                System.out.println("no solution for column " + column);
            }
        }
        
        Queen finalLastQueen = lastQueen; // создаем effectively final копию

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Задача о 8 ферзях");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            
            QueenBoard board = new QueenBoard(finalLastQueen); // используем копию
            frame.add(board);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    
    private static void printSolution(Queen q) {
        System.out.println("Решение задачи о 8 ферзях:");
        Queen current = q;
        
        while (current != null) {
            System.out.println("Ферзь в позиции: строка " + current.getRow() + 
                               ", колонка " + (char)('a' + current.getColumn() - 1));
            current = current.getNeighbor();
        }
        
        // Альтернативный вывод в виде доски в консоли
        printBoardInConsole(q);
    }
    
    private static void printBoardInConsole(Queen q) {
        Queen current = q;
        int[][] queens = new int[8][2];
        int index = 7;
        
        while (current != null && index >= 0) {
            queens[index][0] = current.getRow();
            queens[index][1] = current.getColumn();
            current = current.getNeighbor();
            index--;
        }
        
        System.out.println("\nДоска в консоли:");
        System.out.println("  a b c d e f g h");
        
        for (int row = 8; row >= 1; row--) {
            System.out.print(row + " ");
            
            for (int col = 1; col <= 8; col++) {
                boolean isQueen = false;
                
                for (int qIndex = 0; qIndex < 8; qIndex++) {
                    if (queens[qIndex][0] == row && queens[qIndex][1] == col) {
                        isQueen = true;
                        break;
                    }
                }
                
                if (isQueen)
                    System.out.print("Q ");
                else
                    System.out.print(". ");
            }
            System.out.println(row);
        }
        System.out.println("  a b c d e f g h");
    }
}