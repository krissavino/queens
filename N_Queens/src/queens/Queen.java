package queens;

public class Queen {
    private int row;
    private final int column;
    private Queen neighbor;
    
    // Конструктор
    public Queen(int col, Queen ngh) {
        this.column = col;
        this.neighbor = ngh;
        this.row = 1;
    }
    
    // Геттеры
    public int getRow() { return row; }
    public int getColumn() { return column; }
    public Queen getNeighbor() { return neighbor; }
    
    // Внутренний метод проверки атаки
    private boolean canAttack(int testRow, int testColumn) {
        // Проверка на ту же строку
        if (row == testRow)
            return true;
        
        // Проверка диагонали
        int columnDifference = testColumn - column;
        if ((row + columnDifference == testRow) || (row - columnDifference == testRow))
            return true;
        
        // Проверка соседей
        if (neighbor != null)
            return neighbor.canAttack(testRow, testColumn);
        else
            return false;
    }
    
    // Метод advance
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
    
    // Метод findSolution
    public boolean findSolution() {
        while (neighbor != null && neighbor.canAttack(row, column)) {
            if (!advance())
                return false;
        }
        return true;
    }
}