package ca.othello.othello_v3;

/**
 * This class represents an intelligent AI that plays Othello.
 * It uses the Minimax algorithm with alpha-beta pruning to determine the best move.
 */
public class IntelligentAI {
    private char player;
    private char opponent;
    private int depth;
    private int[] bestMove;
    private int bestScore;

    /**
     * Constructor for the IntelligentAI class.
     *
     * @param player The player character ('W' or 'B') that the AI will play as.
     * @param depth  The depth of the Minimax algorithm.
     */
    public IntelligentAI(char player, int depth) {
        this.player = player;
        this.depth = depth;
        this.bestMove = new int[2];
        this.bestScore = 0;
        this.opponent = (player == 'W') ? 'B' : 'W';
    }

    /**
     * Gets the best move for the AI to make on the given board.
     *
     * @param board The current board state.
     * @return The best move as an array of two integers.
     * @see Board
     * @see #max(Board, int, int, int)
     */
    public int[] getBestMove(Board board) {
        Board clonedBoard = board.cloneBoard();
        this.bestScore = max(clonedBoard, this.depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return this.bestMove;
    }

    /**
     * Gets the best score for the AI on the given board.
     *
     * @return The best score.
     */
    public int getBestScore() {
        return this.bestScore;
    }

    /**
     * The max function of the Minimax algorithm with alpha-beta pruning.
     *
     * @param board The current board state.
     * @param depth The current depth of the algorithm.
     * @param alpha The alpha value for pruning.
     * @param beta  The beta value for pruning.
     * @return The maximum score.
     * @see Board
     * @see Move
     * @see #min(Board, int, int, int)
     * @see #getBestMove(Board)
     */
    private int max(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || board.isGameOver()) {
            return board.evaluate(this.player);
        }

        int maxScore = Integer.MIN_VALUE;
        boolean moveFound = false;

        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (board.isLegalMove(new Move(i, j), this.player)) {
                    moveFound = true;

                    board.makeMove(this.player, new Move(i, j));
                    int score = min(board, depth - 1, alpha, beta);
                    board.undoMove(new Move(i, j));

                    if (score > maxScore) {
                        maxScore = score;
                        if (depth == this.depth) {
                            this.bestMove[0] = i;
                            this.bestMove[1] = j;
                        }
                    }
                    alpha = Math.max(alpha, score);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }

        if (!moveFound) {
            int score = min(board, depth, alpha, beta);
            maxScore = Math.max(maxScore, score);
        }

        return maxScore;
    }

    /**
     * The min function of the Minimax algorithm with alpha-beta pruning.
     *
     * @param board The current board state.
     * @param depth The current depth of the algorithm.
     * @param alpha The alpha value for pruning.
     * @param beta  The beta value for pruning.
     * @return The minimum score.
     * @see Board
     * @see Move
     * @see #max(Board, int, int, int)
     * @see #getBestMove(Board)
     */
    private int min(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || board.isGameOver()) {
            return board.evaluate(this.player);
        }
        int minScore = Integer.MAX_VALUE;
        boolean moveFound = false;

        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (board.isLegalMove(new Move(i, j), this.opponent)) {
                    moveFound = true;

                    board.makeMove(this.opponent, new Move(i, j));
                    int score = max(board, depth - 1, alpha, beta);
                    board.undoMove(new Move(i, j));

                    if (score < minScore) {
                        minScore = score;
                    }
                    beta = Math.min(beta, score);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }

        if (!moveFound) {
            int score = max(board, depth, alpha, beta);
            minScore = Math.min(minScore, score);
        }
        return minScore;
    }

}
