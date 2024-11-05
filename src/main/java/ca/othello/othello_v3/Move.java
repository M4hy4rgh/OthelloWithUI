package ca.othello.othello_v3;

/**
 * Represents a move in the game of Reversi.
 * A move is represented by the row and column of the cell where the player wants to place their piece.
 */
public class Move {
    private int row;
    private int column;

    /**
     * Constructor for a Move object.
     * @param row The row of the cell where the player wants to place their piece.
     * @param column The column of the cell where the player wants to place their piece.
     */
    public Move(int row, int column) {
        this.row = row;
        this.column = column;
    }

    // ============= Getters =============

    /**
     * Get the row of the cell where the player wants to place their piece.
     * @return The row of the cell where the player wants to place their piece.
     */
    public int getRow() { return row; }

    /**
     * Get the column of the cell where the player wants to place their piece.
     * @return The column of the cell where the player wants to place their piece.
     */
    public int getCol() { return column; }
}
