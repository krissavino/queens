package queens;

import java.util.*;

public class DatabaseManager {
    private static final Random rand = new Random();
    private static final String[] COLORS = {"RED", "GREEN", "BLUE"};
    private static final Map<String, QueenState> memoryDB = new HashMap<>();

    // Генерация ключа для хранения
    private static String key(int boardSize, int row, int col) {
        return boardSize + "_" + row + "_" + col;
    }

    public static QueenState getState(int row, int column, int boardSize) {
        String k = key(boardSize, row, column);
        return memoryDB.get(k);
    }

    public static void fillRandomStates(int boardSize) {
        // Заполняем все клетки случайными цветами и стоимостями
        for (int row = 1; row <= boardSize; row++) {
            for (int col = 1; col <= boardSize; col++) {
                String k = key(boardSize, row, col);
                if (!memoryDB.containsKey(k)) {
                    String color = COLORS[0];
                    if(col == 2 && row == 2)
                    	color = COLORS[1];
                    if(col == 4 && row == 5)
                    	color = COLORS[1];
                    int cost = rand.nextInt(100);
                    memoryDB.put(k, new QueenState(row, color, cost));
                }
            }
        }
    }

    public static int calculateTotalCost(int[][] queens, int boardSize) {
        int total = 0;
        for (int i = 0; i < boardSize; i++) {
            int row = queens[i][0];
            int col = queens[i][1];
            if (row > 0) {
                QueenState state = getState(row, col, boardSize);
                if (state != null) total += state.cost;
            }
        }
        return total;
    }
}