package ca.othello.othello_v3;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents the game board for Othello (Reversi)
 * The board is a 8x8 grid with initial pieces placed in the center
 * The board is represented as a 2D char array
 * 'W' - White piece
 * 'B' - Black piece
 * ' ' - Empty cell
 */
public class Board {

    private static final Logger logger = Logger.getLogger(Board.class.getName());
    private static final int SIZE = 8;
    private char[][] board;
    private int numMoves;

    /**
     * Default constructor to initialize the board with initial pieces
     * White pieces at (3,3) and (4,4)
     * Black pieces at (3,4) and (4,3)
     *
     * @see #SIZE
     */
    public Board() {
        this.board = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                this.board[i][j] = ' ';
            }
        }
        this.board[3][3] = 'W';
        this.board[3][4] = 'B';
        this.board[4][3] = 'B';
        this.board[4][4] = 'W';

        this.numMoves = 4;
    }

    /**
     * Copy constructor to create a deep copy of the board
     * Used for creating a copy of the board for the minimax algorithm
     *
     * @return a deep copy of the board
     * @see #SIZE
     */
    public Board cloneBoard() {
        Board newBoard = new Board();
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(this.board[i], 0, newBoard.board[i], 0, SIZE);
        }
        newBoard.numMoves = this.numMoves;
        return newBoard;
    }


    /**
     * Print the current state of the board to the console
     *
     * @see #SIZE
     */
    public void printBoard() {
        System.out.println("   0   1   2   3   4   5   6   7");

        for (int i = 0; i < SIZE; i++) {
            System.out.print(i + " ");

            for (int j = 0; j < SIZE; j++) {
                System.out.print(" " + this.board[i][j] + " ");
                if (j < SIZE - 1) {
                    System.out.print("|");
                }
            }

            System.out.println();

            if (i < SIZE - 1) {
                System.out.print("  ");
                for (int j = 0; j < SIZE; j++) {
                    if (j < SIZE - 1) {
                        System.out.print("---+");
                    } else {
                        System.out.print("---");
                    }

                }
                System.out.println();
            }
        }
    }

    /**
     * Reset the board to the initial state with 4 pieces placed in the center
     * Two white pieces at (3,3) and (4,4)
     * Two black pieces at (3,4) and (4,3)
     *
     * @see #SIZE
     */
    public void resetBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                this.board[i][j] = ' ';
            }
        }

        board[3][3] = 'W';
        board[3][4] = 'B';
        board[4][3] = 'B';
        board[4][4] = 'W';
        logger.info("Board reset to initial state.");
    }

    /**
     * Check if the board is full (no empty cells left)
     *
     * @return true if the board is full, false otherwise
     * @see #SIZE
     */
    public boolean isFull() {
        return this.numMoves == SIZE * SIZE;
    }

    /**
     * Check if a move is legal for the given player
     *
     * @param move   the move to be checked
     * @param player the player (character) making the move
     * @return true if the move is legal, false otherwise
     * @see #isLegalMoveForPlayer(char, Move)
     * @see #SIZE)
     */
    public boolean isLegalMove(Move move, char player) {

        return move.getRow() >= 0 && move.getRow() < SIZE && move.getCol() >= 0 && move.getCol() < SIZE &&
                this.board[move.getRow()][move.getCol()] == ' '
                && isLegalMoveForPlayer(player, move);
    }

    /**
     * Make a move on the board for the given player
     * Update the board state and flip opponent pieces as needed
     *
     * @param player the player (character) making the move
     * @param move   the move to be made
     * @return a list of moves that were flipped after making the move
     * @see #flipPiecesInDirection(char, Move, int, int)
     * @see #canCaptureInDirection(char, char, Move, int, int)
     */
    public List<Move> makeMove(char player, Move move) {
        logger.fine("Making move at (" + move.getRow() + ", " + move.getCol() + ") for player " + player);
        this.board[move.getRow()][move.getCol()] = player;
        this.numMoves++;

        char opponent = (player == 'W') ? 'B' : 'W';

        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };
        List<Move> totalFlippedMoves = new ArrayList<>();

        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];

            if (canCaptureInDirection(player, opponent, move, dx, dy)) {
                logger.fine("Capturing in direction (" + dx + ", " + dy + ")");
                List<Move> flippedMoves = flipPiecesInDirection(player, move, dx, dy);
                totalFlippedMoves.addAll(flippedMoves);
            }
        }

        return totalFlippedMoves;
    }

    /**
     * Undo the last move made on the board
     *
     * @param move the move to be undone
     * @see #SIZE
     */
    public void undoMove(Move move) {
        if (move.getRow() >= 0 && move.getRow() < SIZE && move.getCol() >= 0 && move.getCol() < SIZE
                && this.board[move.getRow()][move.getCol()] != ' ') {
            this.board[move.getRow()][move.getCol()] = ' ';
            this.numMoves--;
        }
    }

    /**
     * Check if the game is over
     * The game is over if the board is full or if neither player has a legal move
     *
     * @return true if the game is over, false otherwise
     * @see #isFull()
     * @see #hasLegalMove(char)
     */
    public boolean isGameOver() {
        if (isFull()) {
            return true;
        }
        boolean noMovesForPlayer = !hasLegalMove('W');
        boolean noMovesForOpponent = !hasLegalMove('B');
        return noMovesForPlayer && noMovesForOpponent;
    }

    /**
     * Get the winner of the game
     * The winner is the player with the most pieces on the board
     *
     * @return the winner of the game ('W' for White, 'B' for Black, 'D' for Draw)
     * @see #getScore(char)
     */
    public char getWinner() {
        int whiteScore = getScore('W');
        int blackScore = getScore('B');

        if (whiteScore > blackScore) {
            return 'W';
        } else if (blackScore > whiteScore) {
            return 'B';
        } else {
            return 'D';
        }
    }

    /**
     * Get the score of the given player
     * The score is the number of pieces on the board for the given player
     *
     * @param player the player (character) whose score is to be calculated
     * @return the score of the player
     * @see #SIZE
     */
    public int getScore(char player) {
        int score = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (this.board[i][j] == player) {
                    score++;
                }
            }
        }
        return score;
    }

    /**
     * Get the piece at the given position on the board
     *
     * @param move the position (row, col) to check
     * @return the piece at the given position
     */
    public char getPiece(Move move) {
        return board[move.getRow()][move.getCol()];
    }

    /**
     * Evaluate the board state for the given player
     * The evaluation function calculates the score difference between the player and the opponent
     * The function also gives additional weight to corners and edges
     *
     * @param player the player (character) for whom the board is to be evaluated
     * @return the evaluation score for the player
     * @see #getScore(char)
     * @see #getPiece(Move)
     */
    public int evaluate(char player) {
        int playerScore = this.getScore(player);
        int opponentScore = this.getScore(player == 'W' ? 'B' : 'W');

        int scoreDifference = playerScore - opponentScore;

        int cornerWeight = 25;
        int edgeWeight = 5;

        // Corners
        int cornerScore = 0;
        if (this.getPiece(new Move(0, 0)) == player) cornerScore += cornerWeight;
        if (this.getPiece(new Move(0, 7)) == player) cornerScore += cornerWeight;
        if (this.getPiece(new Move(7, 0)) == player) cornerScore += cornerWeight;
        if (this.getPiece(new Move(7, 7)) == player) cornerScore += cornerWeight;

        // Edges
        int edgeScore = 0;
        for (int i = 0; i < 8; i++) {
            if (this.getPiece(new Move(0, i)) == player) edgeScore += edgeWeight;
            if (this.getPiece(new Move(7, i)) == player) edgeScore += edgeWeight;
            if (this.getPiece(new Move(i, 0)) == player) edgeScore += edgeWeight;
            if (this.getPiece(new Move(i, 7)) == player) edgeScore += edgeWeight;
        }

        return scoreDifference + cornerScore + edgeScore;
    }

    /**
     * Get the size of the board (number of rows/columns)
     *
     * @return the size of the board
     * @see #SIZE
     */
    public int getSize() {
        return SIZE;
    }

    // Helper Methods

    /**
     * Check if the given player has a legal move available
     *
     * @param player the player (character) to check for
     * @return true if the player has a legal move, false otherwise
     * @see #isLegalMove(Move, char)
     */
    public boolean hasLegalMove(char player) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (isLegalMove(new Move(i, j), player)) {
                    return true;
                }
            }
        }
        System.out.println("No legal moves available for " + player);
        return false;
    }

    /**
     * Check if the given move is a legal move for the given player
     * A move is legal if it captures at least one opponent piece
     *
     * @param player the player (character) making the move
     * @param move   the move to be checked
     * @return true if the move is legal, false otherwise
     * @see #canCaptureInDirection(char, char, Move, int, int)
     */
    private boolean isLegalMoveForPlayer(char player, Move move) {
        char opponent = (player == 'W') ? 'B' : 'W';

        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];

            if (canCaptureInDirection(player, opponent, move, dx, dy)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the given player can capture opponent pieces in the specified direction
     *
     * @param player   the player (character) making the move
     * @param opponent the opponent's piece
     * @param move     the move to be checked
     * @param dx       the change in x-coordinate for the direction
     * @param dy       the change in y-coordinate for the direction
     * @return true if the player can capture opponent pieces in the direction, false otherwise
     * @see #isInBounds(Move)
     */
    private boolean canCaptureInDirection(char player, char opponent, Move move, int dx, int dy) {
        int x = move.getRow() + dx;
        int y = move.getCol() + dy;
        boolean hasOpponentPiece = false;

        logger.fine("Checking direction (" + dx + ", " + dy + ") from (" + move.getRow() + ", " + move.getCol() + ")");

        while (isInBounds(new Move(x, y))) {
            if (this.board[x][y] == opponent) {
                hasOpponentPiece = true;
                logger.fine("Opponent piece at (" + x + ", " + y + ")");
            } else if (this.board[x][y] == player) {
//                return hasOpponentPiece;
                if (hasOpponentPiece) {
                    logger.fine("Found player's own piece at (" + x + ", " + y + ") with opponents in between");
                    return true;
                } else {
                    logger.fine("Found player's own piece at (" + x + ", " + y + ") without any opponents in between");
                    break;
                }
            } else {
                logger.fine("Encountered empty cell at (" + x + ", " + y + "), cannot capture in this direction");
                break;
            }
            x += dx;
            y += dy;
        }
        return false;
    }

    /**
     * Check if the given move is within the bounds of the board
     *
     * @param move the move to be checked
     * @return true if the move is within the bounds, false otherwise
     * @see #SIZE
     */
    private boolean isInBounds(Move move) {
        return move.getRow() >= 0 && move.getRow() < SIZE && move.getCol() >= 0 && move.getCol() < SIZE;
    }

    /**
     * Flip opponent pieces in the specified direction after making a move
     *
     * @param player the player (character) making the move
     * @param move   the move that was made
     * @param dx     the change in x-coordinate for the direction
     * @param dy     the change in y-coordinate for the direction
     * @see #isInBounds(Move)
     */
    private List<Move> flipPiecesInDirection(char player, Move move, int dx, int dy) {
        int x = move.getRow() + dx;
        int y = move.getCol() + dy;
        List<Move> flippedMoves = new ArrayList<>();
        char opponent = (player == 'W') ? 'B' : 'W';

        logger.fine("Flipping pieces in direction (" + dx + ", " + dy + ") starting at (" + x + ", " + y + ")");

        while (isInBounds(new Move(x, y)) && this.board[x][y] == opponent) {
            flippedMoves.add(new Move(x, y));
            x += dx;
            y += dy;
        }
        if (isInBounds(new Move(x, y)) && this.board[x][y] == player) {
            for (Move m : flippedMoves) {
                this.board[m.getRow()][m.getCol()] = player;
                logger.fine("Flipping piece at (" + m.getRow() + ", " + m.getCol() + ") to " + player);
            }
            return flippedMoves;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Get all legal moves for the given player
     *
     * @param color the player (character) for whom the legal moves are to be found
     * @return a list of all legal moves for the player
     * @see #isLegalMove(Move, char)
     */
    public List<Move> getAllLegalMoves(char color) {
        List<Move> legalMoves = new ArrayList<>();
        for (int row = 0; row < getSize(); row++) {
            for (int col = 0; col < getSize(); col++) {
                Move move = new Move(row, col);
                if (isLegalMove(move, color)) {
                    legalMoves.add(move);
                }
            }
        }
        return legalMoves;
    }


}

