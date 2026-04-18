package queens;

import java.util.*;

public class Queen {
    private int row;
    private final int column;
    private Queen neighbor;
    private final String color;          // "RED", "GREEN", "BLUE"
    private final int cost;              // стоимость позиции
    private final int boardSize;         // размер доски (N)

    // Конструктор для поиска решений (используется в рекурсии)
    public Queen(int col, int row, Queen ngh, int boardSize, String color, int cost) {
        this.column = col;
        this.neighbor = ngh;
        this.row = row;
        this.boardSize = boardSize;
        this.color = color;
        this.cost = cost;
    }

    // Конструктор по умолчанию (ряд = 1) – используется в advance()
    public Queen(int col, Queen ngh, int boardSize, String color, int cost) {
        this(col, 1, ngh, boardSize, color, cost);
    }

    // Геттеры
    public int getRow() { 
        return row; 
    }
    
    public int getColumn() { 
        return column; 
    }
    
    public Queen getNeighbor() { 
        return neighbor; 
    }
    
    public String getColor() { 
        return color; 
    }
    
    public int getCost() { 
        return cost; 
    }

    // Проверка геометрической атаки (без учёта цвета)
    public boolean canAttack(int testRow, int testColumn) {
        if (row == testRow)
            return true;
        
        int columnDifference = testColumn - column;
        if ((row + columnDifference == testRow) || (row - columnDifference == testRow))
            return true;
        
        if (neighbor != null)
            return neighbor.canAttack(testRow, testColumn);
        else
            return false;
    }

    // Используется в классическом алгоритме с возвратом
    public boolean advance() {
        if (row < boardSize) {
            row++;
            return findSolution();
        }
        if (neighbor != null && !neighbor.advance())
            return false;
        row = 1;
        return findSolution();
    }

    public boolean findSolution() {
        while (neighbor != null && neighbor.canAttack(row, column)) {
            if (!advance())
                return false;
        }
        return true;
    }

    // Агентный метод поиска всех решений (с учётом цветов, фиксаций и приоритетов)
    public static List<int[][]> findAllSolutions(int boardSize,
                                                 Map<Integer, QueenState> fixedStates,
                                                 Map<Integer, Integer> priorities) {
        // Получаем порядок колонок согласно приоритетам (от высшего к низшему)
        List<Integer> orderedColumns = getOrderedColumns(boardSize, priorities);
        
        List<int[][]> allSolutions = new ArrayList<>();
        
        // Ищем решения для каждого цвета отдельно
        String[] colors = {"RED", "GREEN", "BLUE"};
        
        for (String targetColor : colors) {
            List<int[][]> colorSolutions = new ArrayList<>();
            findSolutionsRecursive(0, orderedColumns, null, boardSize, fixedStates, 
                                  targetColor, colorSolutions);
            allSolutions.addAll(colorSolutions);
        }
        
        return allSolutions;
    }

    private static List<Integer> getOrderedColumns(int boardSize, Map<Integer, Integer> priorities) {
        List<Integer> columns = new ArrayList<>();
        for (int i = 1; i <= boardSize; i++) {
            columns.add(i);
        }
        // Сортировка: чем меньше значение приоритета, тем раньше в списке (выше приоритет)
        columns.sort((c1, c2) -> {
            int p1 = priorities.getOrDefault(c1, Integer.MAX_VALUE);
            int p2 = priorities.getOrDefault(c2, Integer.MAX_VALUE);
            return Integer.compare(p1, p2);
        });
        return columns;
    }

    private static void findSolutionsRecursive(int index,
                                               List<Integer> orderedColumns,
                                               Queen leftmost,
                                               int boardSize,
                                               Map<Integer, QueenState> fixedStates,
                                               String targetColor,
                                               List<int[][]> solutions) {
        if (index == boardSize) {
            // Проверяем, что все ферзи одного цвета
            if (allQueensSameColor(leftmost, targetColor)) {
                int[][] queens = getPositionsArray(leftmost, boardSize);
                solutions.add(queens);
            }
            return;
        }
        
        int column = orderedColumns.get(index);
        QueenState fixed = fixedStates.get(column);
        
        if (fixed != null) {
            // Фиксированная позиция
            Queen newQueen = new Queen(column, fixed.row, leftmost, boardSize, 
                                      fixed.color, fixed.cost);
            // Проверяем только геометрическую совместимость
            if (leftmost == null || !canAttackGeometric(leftmost, fixed.row, column)) {
                findSolutionsRecursive(index + 1, orderedColumns, newQueen, boardSize,
                                     fixedStates, targetColor, solutions);
            }
        } else {
            // Перебираем все ряды
            for (int row = 1; row <= boardSize; row++) {
                QueenState state = DatabaseManager.getState(row, column, boardSize);
                if (state == null) {
                    state = new QueenState(row, "BLUE", 0);
                }
                
                // Пропускаем, если цвет не совпадает с целевым
                if (!state.color.equals(targetColor)) {
                    continue;
                }
                
                Queen newQueen = new Queen(column, row, leftmost, boardSize,
                                          state.color, state.cost);
                if (leftmost == null || !canAttackGeometric(leftmost, row, column)) {
                    findSolutionsRecursive(index + 1, orderedColumns, newQueen, boardSize,
                                         fixedStates, targetColor, solutions);
                }
            }
        }
    }

    // Проверка геометрической атаки (без учёта цвета)
    private static boolean canAttackGeometric(Queen q, int testRow, int testColumn) {
        if (q == null) return false;
        
        // Проверяем горизонталь
        if (q.getRow() == testRow) return true;
        
        // Проверяем диагонали
        int columnDifference = testColumn - q.getColumn();
        if ((q.getRow() + columnDifference == testRow) || 
            (q.getRow() - columnDifference == testRow)) {
            return true;
        }
        
        // Рекурсивно проверяем остальных ферзей
        return canAttackGeometric(q.getNeighbor(), testRow, testColumn);
    }

    // Проверка, что все ферзи в цепочке имеют указанный цвет
    private static boolean allQueensSameColor(Queen q, String targetColor) {
        if (q == null) return true;
        if (!q.getColor().equals(targetColor)) return false;
        return allQueensSameColor(q.getNeighbor(), targetColor);
    }

    // Преобразование цепочки ферзей в массив позиций [N][2]
    public static int[][] getPositionsArray(Queen q, int boardSize) {
        int[][] queens = new int[boardSize][2];
        Queen current = q;
        int index = boardSize - 1;
        while (current != null && index >= 0) {
            queens[index][0] = current.getRow();
            queens[index][1] = current.getColumn();
            current = current.getNeighbor();
            index--;
        }
        return queens;
    }
    
    // Вспомогательный метод для отладки
    @Override
    public String toString() {
        return String.format("Queen[col=%d, row=%d, color=%s, cost=%d]", 
                            column, row, color, cost);
    }
}