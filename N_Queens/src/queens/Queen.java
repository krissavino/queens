package queens;

import java.util.ArrayList;
import java.util.List;

public class Queen {
    private int row;
    private final int column;
    private Queen neighbor;
    
    public Queen(int col, Queen ngh) {
        this.column = col;
        this.neighbor = ngh;
        this.row = 1;
    }
    
    public int getRow() { return row; }
    public int getColumn() { return column; }
    public Queen getNeighbor() { return neighbor; }
    
    private boolean canAttack(int testRow, int testColumn) {
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
    
    public boolean advance() {
        if (row < 8) {
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
    
    public static List<int[][]> findAllSolutions() {
        List<int[][]> solutions = new ArrayList<>();
        findSolutionsRecursive(0, new int[8], solutions);
        return solutions;
    }
    
    private static void findSolutionsRecursive(int col, int[] rows, List<int[][]> solutions) {
        if (col == 8) {
            int[][] solution = new int[8][2];
            for (int i = 0; i < 8; i++) {
                solution[i][0] = rows[i];
                solution[i][1] = i + 1;
            }
            solutions.add(solution);
            return;
        }
        
        for (int row = 1; row <= 8; row++) {
            boolean valid = true;
            for (int prevCol = 0; prevCol < col; prevCol++) {
                int prevRow = rows[prevCol];
                if (prevRow == row || 
                    Math.abs(prevRow - row) == Math.abs(prevCol - col)) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                rows[col] = row;
                findSolutionsRecursive(col + 1, rows, solutions);
            }
        }
    }
    
    public static int[][] getPositionsArray(Queen q) {
        int[][] queens = new int[8][2];
        Queen current = q;
        int index = 7;
        
        while (current != null && index >= 0) {
            queens[index][0] = current.getRow();
            queens[index][1] = current.getColumn();
            current = current.getNeighbor();
            index--;
        }
        return queens;
    }
}