package queens;

public class QueenState {
    public final int row;
    public final String color;
    public final int cost;

    public QueenState(int row, String color, int cost) {
        this.row = row;
        this.color = color;
        this.cost = cost;
    }
}