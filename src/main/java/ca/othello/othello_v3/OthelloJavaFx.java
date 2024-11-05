package ca.othello.othello_v3;

import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;

public class OthelloJavaFx extends Application {

    private Board board = new Board();
    private IntelligentAI ai;
    private boolean isPlayer1Turn;
    private boolean newGame = false;
    private Player player1obj;
    private Player player2obj;
    private ListView<String> history;

    private Label message;
    private Button resetButton;

    private RadioButton intelligent_easy;
    private RadioButton intelligent_hard;
    private RadioButton singlePlayer;
    private RadioButton multiPlayer;

    private StackPane[][] cells = new StackPane[8][8];

    private static final Logger logger = Logger.getLogger(OthelloJavaFx.class.getName());
    private boolean gameStarted = false;


    @Override
    public void start(Stage stage) throws Exception {

        //-----------------components and Fields-----------------

        Label headerText = new Label("Othello");
        headerText.setAlignment(Pos.CENTER);
        headerText.setPrefSize(500, 10);
        headerText.setStyle("-fx-font-size: 38px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        message = new Label();
        message.setAlignment(Pos.CENTER);
        message.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        Label player1 = new Label("Player 1:");
        TextField player1Name = new TextField();
        player1Name.setFocusTraversable(false);

        Label player2 = new Label("Player 2:");
        TextField player2Name = new TextField();
        player2Name.setFocusTraversable(false);

        Label gameMode = new Label("Game Mode:");
        singlePlayer = new RadioButton("Single-Player");
        singlePlayer.setFocusTraversable(false);

        multiPlayer = new RadioButton("Multi-Player");
        multiPlayer.setFocusTraversable(false);

        Label AIMode = new Label("AI Mode:");
        RadioButton weak = new RadioButton("Weak");
        weak.setFocusTraversable(false);
        weak.setDisable(true);

        intelligent_easy = new RadioButton("Intelligent AI (Easy)");
        intelligent_easy.setFocusTraversable(false);
        intelligent_easy.setDisable(true);

        intelligent_hard = new RadioButton("Intelligent AI (Hard)");
        intelligent_hard.setFocusTraversable(false);
        intelligent_hard.setDisable(true);

        Label player1Symbol_Label = new Label("Player 1 Symbol:");
        RadioButton player1Symbol_black = new RadioButton("Black");
        player1Symbol_black.setFocusTraversable(false);
        RadioButton player1Symbol_white = new RadioButton("White");
        player1Symbol_white.setFocusTraversable(false);

        Label Player2Symbol_Label = new Label("Player 2 Symbol:");
        RadioButton player2Symbol_black_2 = new RadioButton("Black");
        player2Symbol_black_2.setFocusTraversable(false);
        RadioButton player2Symbol_white_2 = new RadioButton("White");
        player2Symbol_white_2.setFocusTraversable(false);

        Button startButton = new Button("Start");
        startButton.setPrefSize(100, 30);
        startButton.setFocusTraversable(false);
        startButton.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-border-insets: 0; -fx-background-insets: 0;" +
                "-fx-border-radius: 5; -fx-background-radius: 5;");


        resetButton = new Button("Reset");
        resetButton.setPrefSize(100, 30);
        resetButton.setFocusTraversable(false);
        resetButton.setDisable(true);
        resetButton.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-border-insets: 0; -fx-background-insets: 0;" +
                "-fx-border-radius: 5; -fx-background-radius: 5;");


        Label[] Labels = {player1, player2, gameMode, AIMode, player1Symbol_Label, Player2Symbol_Label};
        for (Label l : Labels) {
            l.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        }

        RadioButton[] radioButtons = {singlePlayer, multiPlayer, weak, intelligent_easy, intelligent_hard, player1Symbol_black, player1Symbol_white, player2Symbol_black_2, player2Symbol_white_2};
        for (RadioButton r : radioButtons) {
            r.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-border-insets: 0; -fx-background-insets: 0;");
        }

        initializeBoardCells(cells);

        //---------------------------------layout---------------------------------
        BorderPane borderPane = new BorderPane();
        VBox headerContainer = new VBox();
        TilePane boardContainer = new TilePane();
        VBox historyContainer = new VBox();
        history = new ListView<>();
        VBox playerVboxContainer = new VBox();
        VBox playerVbox = new VBox();
        HBox player1NameHbox = new HBox();
        HBox player2NameHbox = new HBox();
        HBox gameModeHbox = new HBox();
        HBox AILevelHbox = new HBox();
        HBox player1SymbolHbox = new HBox();
        HBox player2SymbolHbox = new HBox();
        HBox buttonHbox = new HBox();

        //-----------------------------styling---------------------------------

        BackgroundFill background_fill = new BackgroundFill(Color.rgb(254, 254, 254, 0.3),
                new CornerRadii(10), Insets.EMPTY);

        borderPane.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        BorderPane.setAlignment(headerText, Pos.CENTER);
        headerContainer.setAlignment(Pos.CENTER);
        borderPane.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setMargin(playerVboxContainer, new Insets(0, 10, 0, 0));


        boardContainer.setPrefColumns(8);
        boardContainer.setPrefRows(8);
        boardContainer.setAlignment(Pos.CENTER);
        boardContainer.setMaxWidth(620);
        boardContainer.setMaxHeight(650);
        boardContainer.setHgap(1);
        boardContainer.setVgap(1);

        ToggleGroup gameModeGroup = new ToggleGroup();
        singlePlayer.setToggleGroup(gameModeGroup);
        multiPlayer.setToggleGroup(gameModeGroup);

        ToggleGroup AIModeGroup = new ToggleGroup();
        weak.setToggleGroup(AIModeGroup);
        intelligent_easy.setToggleGroup(AIModeGroup);
        intelligent_hard.setToggleGroup(AIModeGroup);

        ToggleGroup player1SymbolGroup = new ToggleGroup();
        player1Symbol_black.setToggleGroup(player1SymbolGroup);
        player1Symbol_white.setToggleGroup(player1SymbolGroup);

        ToggleGroup player2SymbolGroup = new ToggleGroup();
        player2Symbol_black_2.setToggleGroup(player2SymbolGroup);
        player2Symbol_white_2.setToggleGroup(player2SymbolGroup);


        playerVboxContainer.setAlignment(Pos.CENTER);
        playerVboxContainer.setSpacing(10);
        playerVboxContainer.setPadding(new Insets(0, 0, 0, 20));

        playerVbox.setAlignment(Pos.CENTER);
        playerVbox.setSpacing(10);
        playerVbox.setPadding(new Insets(10, 10, 10, 10));
        playerVbox.setMaxHeight(400);
        playerVbox.setBackground(new Background(background_fill));

        player1NameHbox.setSpacing(10);
        player2NameHbox.setSpacing(10);

        gameModeHbox.setSpacing(10);
        gameModeHbox.setPadding(new Insets(10, 0, 5, 0));

        AILevelHbox.setSpacing(10);
        AILevelHbox.setPadding(new Insets(0, 0, 6, 0));

        player1SymbolHbox.setSpacing(10);
        player1SymbolHbox.setPadding(new Insets(0, 0, 10, 0));
        player2SymbolHbox.setSpacing(10);
        player2SymbolHbox.setPadding(new Insets(0, 0, 10, 0));

        buttonHbox.setSpacing(10);
        buttonHbox.setAlignment(Pos.CENTER);

        historyContainer.setBackground(new Background(background_fill));
        historyContainer.setPrefSize(200, 260);
        historyContainer.setMaxHeight(260);
        historyContainer.setStyle("-fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 10 0 0 10; ");

        history.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #000000;" +
                "-fx-background-color: transparent; -fx-control-inner-background: transparent;  -fx-background-radius: 10 0 0 10;");
        history.setBackground(new Background(background_fill));
        history.getStyleClass().add("custom-list-view");
        history.getStyleClass().add("custom-scroll-bar");
        history.getSelectionModel().clearSelection();
        history.setFocusTraversable(false);


        //-----------------add components to layout-----------------
        headerContainer.getChildren().addAll(headerText, message);
        borderPane.setTop(headerContainer);
        borderPane.setCenter(boardContainer);

        for (StackPane[] row : cells) {
            for (StackPane cell : row) {
                boardContainer.getChildren().add(cell);
            }
        }

        player1NameHbox.getChildren().addAll(player1, player1Name);
        player2NameHbox.getChildren().addAll(player2, player2Name);

        gameModeHbox.getChildren().addAll(gameMode, singlePlayer, multiPlayer);

        AILevelHbox.getChildren().addAll(AIMode, weak, intelligent_easy, intelligent_hard);

        player1SymbolHbox.getChildren().addAll(player1Symbol_Label, player1Symbol_black, player1Symbol_white);

        player2SymbolHbox.getChildren().addAll(Player2Symbol_Label, player2Symbol_black_2, player2Symbol_white_2);

        buttonHbox.getChildren().addAll(startButton, resetButton);

        playerVbox.getChildren().addAll(player1NameHbox, player2NameHbox, gameModeHbox, AILevelHbox,
                player1SymbolHbox, player2SymbolHbox, buttonHbox);

        historyContainer.getChildren().add(history);

        playerVboxContainer.getChildren().addAll(playerVbox, new Label("History") {{
            setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        }}, historyContainer);

        borderPane.setLeft(playerVboxContainer);


        //-----------------scene and stage-----------------
        try {
            Scene scene = new Scene(borderPane, 1140, 750);
            stage.setMinWidth(1140);
            stage.setMinHeight(750);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/styles.css")).toExternalForm());

            stage.setTitle("Welcome to Othello!");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //-----------------event handling-----------------

        player1SymbolGroup.selectedToggleProperty().addListener((observable1, oldValue1, newValue1) -> {
            if (!newGame) {
                if (newValue1 == player1Symbol_white) {
                    player2Symbol_black_2.setSelected(true);
                } else {
                    player2Symbol_white_2.setSelected(true);
                }
            }
        });

        player2SymbolGroup.selectedToggleProperty().addListener((observable2, oldValue2, newValue2) -> {
            if (!newGame) {
                if (newValue2 == player2Symbol_white_2) {
                    player1Symbol_black.setSelected(true);
                } else {
                    player1Symbol_white.setSelected(true);
                }
            }
        });

        gameModeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == singlePlayer) {
                player1Symbol_white.setDisable(false);
                player1Symbol_black.setDisable(false);

                player2Name.setText("Computer");
                player2Name.setDisable(true);
                player2Symbol_white_2.setDisable(true);
                player2Symbol_black_2.setDisable(true);

                weak.setDisable(false);
                intelligent_easy.setDisable(false);
                intelligent_hard.setDisable(false);

            } else if (newValue == multiPlayer) {
                if (player2Name.getText().equals("Computer")) {
                    player2Name.setText("");
                }
                player1Symbol_white.setDisable(false);
                player1Symbol_black.setDisable(false);

                player2Name.setDisable(false);
                player2Symbol_white_2.setDisable(false);
                player2Symbol_black_2.setDisable(false);

                weak.setDisable(true);
                intelligent_easy.setDisable(true);
                intelligent_hard.setDisable(true);

                for (Toggle rdb : AIModeGroup.getToggles()) {
                    rdb.setSelected(false);
                }
            }
        });


        startButton.setOnAction(e -> {
            if (player1Name.getText().trim().isEmpty() || player2Name.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Player name is empty!");
                alert.setContentText("Please enter a name for both players!");
                alert.showAndWait();
            } else if (player1Name.getText().equals(player2Name.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Player name is the same!");
                alert.setContentText("Please enter a different name for one of the players!");
                alert.showAndWait();

            } else if (!singlePlayer.isSelected() && !multiPlayer.isSelected()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Game mode is not selected!");
                alert.setContentText("Please select a game mode!");
                alert.showAndWait();

            } else if (singlePlayer.isSelected() && (!weak.isSelected() && !intelligent_easy.isSelected() && !intelligent_hard.isSelected())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("AI level is not selected!");
                alert.setContentText("Please select an AI level!");
                alert.showAndWait();

            } else if (!player1Symbol_white.isSelected() && !player1Symbol_black.isSelected() || !player2Symbol_white_2.isSelected() && !player2Symbol_black_2.isSelected()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Symbol is not selected!");
                alert.setContentText("Please select a symbol for both players!");
                alert.showAndWait();

            } else if ((player1Symbol_black.isSelected() && player2Symbol_black_2.isSelected()) ||
                    (player1Symbol_white.isSelected() && player2Symbol_white_2.isSelected())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Symbol is already taken!");
                alert.setContentText("Please select a different symbol for one of the players!");
                alert.showAndWait();
            } else {
                for (StackPane[] cellRow : cells) {
                    for (StackPane cell : cellRow) {
                        cell.setDisable(false);
                    }
                }

                player1Name.setDisable(true);
                player2Name.setDisable(true);
                singlePlayer.setDisable(true);
                multiPlayer.setDisable(true);

                weak.setDisable(true);
                intelligent_easy.setDisable(true);
                intelligent_hard.setDisable(true);

                player1Symbol_white.setDisable(true);
                player1Symbol_black.setDisable(true);
                player2Symbol_white_2.setDisable(true);
                player2Symbol_black_2.setDisable(true);

                resetButton.setDisable(false);

                if (player1Symbol_white.isSelected()) {
                    player1obj = new Player(player1Name.getText(), 'W');
                    player2obj = new Player(player2Name.getText(), 'B');
                } else {
                    player1obj = new Player(player1Name.getText(), 'B');
                    player2obj = new Player(player2Name.getText(), 'W');
                }
                gameStarted = true;

                logger.info("Player1 (" + player1obj.getName() + ") is " + (player1obj.getColor() == 'B' ? "Black" : "White") + ".");
                logger.info("Player2 (" + player2obj.getName() + ") is " + (player2obj.getColor() == 'B' ? "Black" : "White") + ".");

                isPlayer1Turn = player1obj.getColor() == 'B';
                logger.info("Game started. " + (isPlayer1Turn ? player1obj.getName() + " (B)" : player2obj.getName() + " (W)") + " goes first.");
                message.setText(isPlayer1Turn ? player1obj.getName() + "'s turn" : player2obj.getName() + "'s turn");

                refreshBoardUI(cells);

                if (!isPlayer1Turn && singlePlayer.isSelected()) {
                    handleAIMove(cells);
                }

            }
        });

        resetButton.setOnAction(e -> {
            newGame = true;

            for (StackPane[] cellRow : cells) {
                for (StackPane cell : cellRow) {
                    cell.setDisable(true);
                    Circle disk = (Circle) cell.getChildren().get(1);
                    disk.setVisible(false);
                }
            }


            message.setText("");

            player1Name.setDisable(false);
            player2Name.setDisable(false);
            player1Name.setText("");
            player2Name.setText("");
            player1.setStyle("-fx-background-color: none; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #000000;");
            player2.setStyle("-fx-background-color: none; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #000000;");

            singlePlayer.setDisable(false);
            multiPlayer.setDisable(false);
            if (!newGame) {
                weak.setDisable(false);
                intelligent_easy.setDisable(false);
                intelligent_hard.setDisable(false);
            }
            player1Symbol_white.setDisable(true);
            player1Symbol_black.setDisable(true);
            player2Symbol_white_2.setDisable(true);
            player2Symbol_black_2.setDisable(true);

            singlePlayer.setSelected(false);
            multiPlayer.setSelected(false);

            player1Symbol_white.setSelected(false);
            player1Symbol_black.setSelected(false);
            player2Symbol_white_2.setSelected(false);
            player2Symbol_black_2.setSelected(false);

            weak.setSelected(false);
            intelligent_easy.setSelected(false);
            intelligent_hard.setSelected(false);

            board.resetBoard();
            history.getItems().clear();

            history.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #000000;" +
                    "-fx-background-color: transparent; -fx-control-inner-background: transparent;  -fx-background-radius: 10 0 0 10;");
            history.setBackground(new Background(background_fill));

            player1obj = null;
            player2obj = null;

            refreshBoardUI(cells);

            gameStarted = false;
            newGame = false;
            logger.info("Game reset.");
        });

        //----------------- Helper -----------------
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                final int row = i;
                final int col = j;

                cells[i][j].setOnMouseClicked(e -> {
                    handleCellClick(row, col);
                });
            }
        }

        history.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setDisable(true);
                    setMouseTransparent(true);
                }
            }
        });

    }


    /**
     * Initializes the 8x8 grid of board cells using StackPane.
     *
     * @param cells The 8x8 array to populate with StackPanes.
     */
    private void initializeBoardCells(StackPane[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(70, 70);

                Rectangle background = new Rectangle(70, 70);
                background.setFill(Color.GREEN);
                background.setStroke(Color.BLACK);
                background.setStrokeWidth(1);
                Circle disk = new Circle(25);
                disk.setVisible(false);

                Circle highlight = new Circle(10);
                highlight.setFill(Color.YELLOW.deriveColor(1, 1, 1, 0.6));
                highlight.setVisible(false);

                cell.getChildren().addAll(background, disk, highlight);

                final int row = i;
                final int col = j;
                cell.setOnMouseClicked(e -> handleCellClick(row, col));
                cell.setOnMouseEntered(e -> {
                    background.setEffect(new Glow(0.2));
                    cell.setCursor(Cursor.HAND);

                });
                cell.setOnMouseExited(e -> {
                    background.setEffect(null);
                    cell.setCursor(Cursor.DEFAULT);
                });


                cells[i][j] = cell;

            }
        }

    }

    /**
     * Refreshes the entire board UI to match the current state of the game board.
     * It iterates through all cells and updates the button texts accordingly.
     *
     * @param cells The 8x8 grid of cells representing the board.
     */
    private void refreshBoardUI(StackPane[][] cells) {
        logger.info("Refreshing board UI.");

        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                char piece = board.getPiece(new Move(i, j));
                StackPane cell = cells[i][j];
                Circle disk = (Circle) cell.getChildren().get(1);

                if (piece == 'B') {
                    disk.setFill(Color.BLACK);
                    disk.setVisible(true);
                    logger.fine("Placed Black disk at (" + i + ", " + j + ").");
                } else if (piece == 'W') {
                    disk.setFill(Color.WHITE);
                    disk.setVisible(true);
                    logger.fine("Placed White disk at (" + i + ", " + j + ").");
                } else {
                    disk.setVisible(false);
                    logger.fine("Cleared disk at (" + i + ", " + j + ").");
                }
            }
        }

        highlightValidMoves();
    }

    /**
     * Adds a move to the history ListView.
     *
     * @param playerName The name of the player who made the move.
     * @param row        The row of the move.
     * @param col        The column of the move.
     */
    private void addMoveToHistory(String playerName, int row, int col) {
        String moveDescription = (row == -1 && col == -1)
                ? playerName + " passed."
                : playerName + " played at: row " + row + ", col " + col;

        int blackScore = board.getScore('B');
        int whiteScore = board.getScore('W');

        String fullDescription = moveDescription + " | Scores => Black: " + blackScore + ", White: " + whiteScore;

        history.getItems().add(fullDescription);
        history.scrollTo(history.getItems().size() - 1);
    }

    /**
     * Updates the turn message label based on the current player's turn.
     */
    private void updateTurnMessage() {
        String currentPlayer = isPlayer1Turn ? player1obj.getName() : player2obj.getName();
        message.setText(currentPlayer + "'s turn");
        logger.info("Turn updated: " + currentPlayer + " is now playing.");
    }

    /**
     * Displays a pass message when a player has no legal moves.
     *
     * @param playerName The name of the player who must pass.
     */
    private void showPassMessage(String playerName) {
        message.setText(playerName + " has no legal moves and must pass.");
        addMoveToHistory(playerName, -1, -1);
        logger.info(playerName + " has to pass.");
    }

    /**
     * Checks if the game is over and displays the result along with final scores.
     *
     * @return true if the game is over, false otherwise.
     */
    private boolean checkGameOver() {
        if (board.isGameOver()) {
            char winner = board.getWinner();
            String resultMessage;
            String finalScoreMessage;
            int blackScore = board.getScore('B');
            int whiteScore = board.getScore('W');

            if (winner == 'D') {
                resultMessage = "It's a draw.";
            } else {
                String winnerName = (winner == player1obj.getColor()) ? player1obj.getName() : player2obj.getName();
                resultMessage = "The winner is: " + winnerName;
            }

            finalScoreMessage = "Final Scores:\nBlack: " + blackScore + "\nWhite: " + whiteScore;


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText(resultMessage + "\n\n" + finalScoreMessage);
            alert.showAndWait();

            resetButton.fire();

            return true;
        }
        return false;
    }


    /**
     * Initiates the AI move on a separate thread to prevent UI blocking.
     *
     * @param cells The 8x8 grid of cells representing the board.
     */
    private void handleAIMove(StackPane[][] cells) {
        Task<int[]> aiTask = new Task<>() {
            @Override
            protected int[] call() {
                if (intelligent_easy.isSelected()) {
                    ai = new IntelligentAI(player2obj.getColor(), 3);
                } else if (intelligent_hard.isSelected()) {
                    ai = new IntelligentAI(player2obj.getColor(), 5);
                }
                return ai != null ? ai.getBestMove(board) : getRandomMove();
            }
        };

        aiTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                int[] aiMove = aiTask.getValue();
                if (aiMove != null) {
                    logger.info("AI selected move: (" + aiMove[0] + ", " + aiMove[1] + ")");
                    List<Move> flippedMoves = board.makeMove(player2obj.getColor(), new Move(aiMove[0], aiMove[1]));
                    addMoveToHistory(player2obj.getName(), aiMove[0], aiMove[1]);
                    boolean gameOver = checkGameOver();
                    if (gameOver) {
                        return;
                    }

                    for (Move flippedMove : flippedMoves) {
                        Circle disk = (Circle) cells[flippedMove.getRow()][flippedMove.getCol()].getChildren().get(1);
                        Color fromColor = player2obj.getColor() == 'B' ? Color.WHITE : Color.BLACK;
                        Color toColor = player2obj.getColor() == 'B' ? Color.BLACK : Color.WHITE;
                        animateDiskFlip(disk, fromColor, toColor);
                        logger.fine("Flipped disk at (" + flippedMove.getRow() + ", " + flippedMove.getCol() + ") to " + toColor + ".");
                    }

                    isPlayer1Turn = true;
                    updateTurnMessage();

                    refreshBoardUI(cells);

                    checkAndHandlePass(cells);
                } else {
                    logger.info("AI has no legal moves and must pass.");
                    showPassMessage(player2obj.getName());
                    clearHighlights();

                    isPlayer1Turn = true;
                    updateTurnMessage();

                    refreshBoardUI(cells);

                    checkAndHandlePass(cells);
                }
            });
        });

        aiTask.setOnFailed(event -> {
            aiTask.getException().printStackTrace();
        });

        new Thread(aiTask).start();
    }


    /**
     * Gets a random move for the weak AI.
     *
     * @return The random move as an array [row, col], or null if no move is found.
     */
    private int[] getRandomMove() {
        List<Move> legalMoves = board.getAllLegalMoves(player2obj.getColor());
        logger.info("AI has " + legalMoves.size() + " legal moves.");

        if (legalMoves.isEmpty()) {
            return null;
        }

        Random rand = new Random();
        Move selectedMove = legalMoves.get(rand.nextInt(legalMoves.size()));
        logger.info("AI selects move: (" + selectedMove.getRow() + ", " + selectedMove.getCol() + ")");

        return new int[]{selectedMove.getRow(), selectedMove.getCol()};
    }

    /**
     * Checks if the current player can make any legal moves. If not, passes the turn to the opponent.
     * If neither player can move, concludes the game.
     *
     * @param buttons The 8x8 grid of buttons representing the board.
     */
    private void checkAndHandlePass(StackPane[][] buttons) {
        if (board.isGameOver()) {
            return;
        }

        if (isPlayer1Turn) {
            if (board.hasLegalMove(player1obj.getColor())) {
                return;
            } else if (board.hasLegalMove(player2obj.getColor())) {
                showPassMessage(player1obj.getName());
                isPlayer1Turn = false;
                updateTurnMessage();

                if (singlePlayer.isSelected()) {
                    handleAIMove(buttons);
                }
            } else {
                checkGameOver();
            }
        } else {
            if (board.hasLegalMove(player2obj.getColor())) {
                if (singlePlayer.isSelected()) {
                    handleAIMove(buttons);
                }
            } else if (board.hasLegalMove(player1obj.getColor())) {
                showPassMessage(player2obj.getName());
                isPlayer1Turn = true;
                updateTurnMessage();
            } else {
                checkGameOver();
            }
        }
    }

    /**
     * Handles the cell click event.
     *
     * @param row The row index of the clicked cell.
     * @param col The column index of the clicked cell.
     */
    private void handleCellClick(int row, int col) {
        if (player1obj == null || player2obj == null) {
            logger.warning("Players are not initialized. Please start a new game.");
            return;
        }

        char currentColor = isPlayer1Turn ? player1obj.getColor() : player2obj.getColor();
        logger.info("Player " + (isPlayer1Turn ? "1" : "2") + " (" + currentColor + ") clicked on (" + row + ", " + col + ").");


        if (board.isLegalMove(new Move(row, col), currentColor)) {
            Player currentPlayer = isPlayer1Turn ? player1obj : player2obj;
            List<Move> flippedMoves = board.makeMove(currentColor, new Move(row, col));
            addMoveToHistory(currentPlayer.getName(), row, col);
            refreshBoardUI(cells);

            for (Move flippedMove : flippedMoves) {
                Circle disk = (Circle) cells[flippedMove.getRow()][flippedMove.getCol()].getChildren().get(1);
                Color fromColor = currentColor == 'B' ? Color.WHITE : Color.BLACK;
                Color toColor = currentColor == 'B' ? Color.BLACK : Color.WHITE;
                animateDiskFlip(disk, fromColor, toColor);
                logger.fine("Flipped disk at (" + flippedMove.getRow() + ", " + flippedMove.getCol() + ") to " + toColor + ".");
            }

            boolean gameOver = checkGameOver();
            if (gameOver) {
                return;
            }

            isPlayer1Turn = !isPlayer1Turn;
            logger.info("Turn toggled. isPlayer1Turn is now " + isPlayer1Turn + ".");
            updateTurnMessage();

            refreshBoardUI(cells);

            checkAndHandlePass(cells);
        } else {
            logger.warning("Invalid move attempted at (" + row + ", " + col + ").");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Move!");
            alert.setContentText("Please make a valid move!");
            alert.showAndWait();
        }
    }

    /**
     * Animates the flipping of a disk by rotating it along the Y-axis.
     *
     * @param diskNode  The Circle representing the disk.
     * @param fromColor The original color of the disk.
     * @param toColor   The new color of the disk after flipping.
     */
    private void animateDiskFlip(Object diskNode, Color fromColor, Color toColor) {
        System.out.println("Animating disk flip from " + fromColor + " to " + toColor);

        if (!(diskNode instanceof Circle)) return;
        Circle disk = (Circle) diskNode;

        RotateTransition rotateOut = new RotateTransition(Duration.millis(150), disk);
        rotateOut.setAxis(new Point3D(0, 1, 0));
        rotateOut.setFromAngle(0);
        rotateOut.setToAngle(90);

        rotateOut.setOnFinished(event -> {
            disk.setFill(toColor);
        });

        RotateTransition rotateIn = new RotateTransition(Duration.millis(150), disk);
        rotateIn.setAxis(new Point3D(0, 1, 0));
        rotateIn.setFromAngle(270);
        rotateIn.setToAngle(360);

        SequentialTransition flipTransition = new SequentialTransition(rotateOut, rotateIn);
        flipTransition.play();
    }

    private void highlightValidMoves() {
        if (!gameStarted || player1obj == null || player2obj == null) {
            logger.info("Players are not initialized. Skipping move highlighting.");
            clearHighlights();
            return;
        }

        if (!isCurrentPlayerHuman()) {
            clearHighlights();
            logger.info("AI's turn. Highlights cleared.");
            return;
        }

        char currentColor = isPlayer1Turn ? player1obj.getColor() : player2obj.getColor();
        System.out.println("Current Color: " + currentColor);
        String currentPlayerName = isPlayer1Turn ? player1obj.getName() : player2obj.getName();
        logger.info("Highlighting valid moves for " + currentPlayerName + " (" + currentColor + ").");

        List<Move> legalMoves = board.getAllLegalMoves(currentColor);
        logger.info("Current Player: " + currentColor + " | Legal Moves Count: " + legalMoves.size());

        clearHighlights();

        for (Move move : legalMoves) {
            int i = move.getRow();
            int j = move.getCol();
            StackPane cell = cells[i][j];

            if (cell.getChildren().size() < 3) {
                logger.warning("Cell (" + i + ", " + j + ") does not have enough children to highlight.");
                continue;
            }
            Circle highlight = (Circle) cell.getChildren().get(2);
            if (highlight == null) {
                logger.warning("Highlight circle is null for cell (" + i + ", " + j + ").");
                continue;
            }
            highlight.setVisible(true);
            Tooltip tooltip = new Tooltip("Row: " + i + ", Col: " + j);
            Tooltip.install(cell, tooltip);
            logger.info("Highlighting move at (" + i + ", " + j + ")");
        }
    }

    /**
     * Clears all previous highlights from the board.
     */
    private void clearHighlights() {
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                StackPane cell = cells[i][j];
                if (cell.getChildren().size() < 3) {
                    logger.warning("Cell (" + i + ", " + j + ") does not have enough children to clear highlight.");
                    continue;
                }
                Circle highlight = (Circle) cell.getChildren().get(2);
                highlight.setVisible(false);
            }
        }
    }

    /**
     * Determines if the current player is human.
     *
     * @return true if the current player is human; false if AI.
     */
    private boolean isCurrentPlayerHuman() {
        if (singlePlayer.isSelected()) {
            return isPlayer1Turn;
        } else {
            return true;
        }
    }

    public static void main(String[] args) {

        launch(args);
    }
}


