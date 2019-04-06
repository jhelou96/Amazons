package com.joeyhelou.amazons;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Random;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameController {
	@FXML
	private GridPane gpBoard;
	@FXML
	private ListView<String> lvLogs;
	
	private ImageView whiteQueen1;
	private ImageView whiteQueen2;
	private ImageView whiteQueen3;
	private ImageView whiteQueen4;
	private ImageView blackQueen1;
	private ImageView blackQueen2;
	private ImageView blackQueen3;
	private ImageView blackQueen4;
	
	private boolean gameFinished = false;
	private String turn = "white";
	private int part = 1;
	private int selectedRow = -1;
	private int selectedColumn = -1;
	private ObservableList<String> logs;
	private char[] columnsNotation = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
	private int[] rowsNotation = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
	private String gameMode;
	
	private Stage stage;
	
	@FXML
    private void initialize() throws URISyntaxException {
		Image whiteQueen = new Image(getClass().getResource("/images/queen-white.png").toURI().toString());
		Image blackQueen = new Image(getClass().getResource("/images/queen-black.png").toURI().toString());
		
		whiteQueen1 = new ImageView(whiteQueen);
		VBox square = (VBox) getBoardSquare(9, 4);
		square.getChildren().add(whiteQueen1);
		
		whiteQueen2 = new ImageView(whiteQueen);
		square = (VBox) getBoardSquare(9, 7);
		square.getChildren().add(whiteQueen2);
		
		whiteQueen3 = new ImageView(whiteQueen);
		square = (VBox) getBoardSquare(6, 1);
		square.getChildren().add(whiteQueen3);
		
		whiteQueen4 = new ImageView(whiteQueen);
		square = (VBox) getBoardSquare(6, 10);
		square.getChildren().add(whiteQueen4);
		
		blackQueen1 = new ImageView(blackQueen);
		square = (VBox) getBoardSquare(0, 4);
		square.getChildren().add(blackQueen1);
		
		blackQueen2 = new ImageView(blackQueen);
		square = (VBox) getBoardSquare(0, 7);
		square.getChildren().add(blackQueen2);
		
		blackQueen3 = new ImageView(blackQueen);
		square = (VBox) getBoardSquare(3, 1);
		square.getChildren().add(blackQueen3);
		
		blackQueen4 = new ImageView(blackQueen);
		square = (VBox) getBoardSquare(3, 10);
		square.getChildren().add(blackQueen4);
		
		logs = FXCollections.observableArrayList();
		logs.add(0, "New game started");
		logs.add(0, "White player turn");
		lvLogs.setItems(logs);
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setGameMode(String gameMode) {
		this.gameMode = gameMode;
	}
	
	@FXML
	private void squareClickedHandler(MouseEvent event) {
		if(gameFinished)
			return;
		
		VBox square = (VBox) event.getSource();
		
		// If square is not empty
		if(square.getChildren().size() > 0) {
			// If black queen selected and black player turn or if white queen selected and white player turn
			if(((square.getChildren().get(0) == blackQueen1 || square.getChildren().get(0) == blackQueen2 ||
					square.getChildren().get(0) == blackQueen3 || square.getChildren().get(0) == blackQueen4) &&
					turn.equals("black")) || ((square.getChildren().get(0) == whiteQueen1 || square.getChildren().get(0) == whiteQueen2 ||
					square.getChildren().get(0) == whiteQueen3 || square.getChildren().get(0) == whiteQueen4) &&
					turn.equals("white")) && part == 1) {
				highlightPossibleMoves(square);
				selectedRow = GridPane.getRowIndex(square);
				selectedColumn = GridPane.getColumnIndex(square);
			} 
		} else if(selectedRow != -1 && selectedColumn != -1 && part == 1 && checkValidMove(square)) { // If player selected a queen and made a move
			// Remove queen from old square
			VBox squareToBeCleared = (VBox) getBoardSquare(selectedRow, selectedColumn);
			ImageView selectedQueen = (ImageView) squareToBeCleared.getChildren().get(0);
			squareToBeCleared.getChildren().clear();
			
			// Move queen to new square
			square.getChildren().add(selectedQueen);
			
			// Save move in logs
			logs.add(0, "Move " + columnsNotation[GridPane.getColumnIndex(squareToBeCleared) - 1] + rowsNotation[GridPane.getRowIndex(squareToBeCleared)] + " to " + columnsNotation[GridPane.getColumnIndex(square) - 1] + rowsNotation[GridPane.getRowIndex(square)]);
			
			// Reset all values
			selectedRow = -1;
			selectedColumn = -1;
			part = 2;
			highlightPossibleMoves(square);
		} else if(part == 2 && checkValidMove(square)) { // After a queen is moved, player can throw a spear
			square.getChildren().add(new Text("X"));
					
			// Reset
			resetBoardSquareColors();
			part = 1;
			if(turn == "white")
				turn = "black";
			else
				turn = "white";
			
			// Save move in logs
			logs.add(0, "Spear on " + columnsNotation[GridPane.getColumnIndex(square) - 1] + rowsNotation[GridPane.getRowIndex(square)]);
			logs.add(0, (turn.equals("white") ? "White" : "Black") + " player turn");
			
			// Check if next player has valid moves
			if(turn == "white" && !checkIfPlayerHasValidMoves(whiteQueen1) && !checkIfPlayerHasValidMoves(whiteQueen2) && !checkIfPlayerHasValidMoves(whiteQueen3) && !checkIfPlayerHasValidMoves(whiteQueen4)) {
				logs.add(0, "Black player won !");
				gameFinished = true;
			} else if(turn == "black" && !checkIfPlayerHasValidMoves(blackQueen1) && !checkIfPlayerHasValidMoves(blackQueen2) && !checkIfPlayerHasValidMoves(blackQueen3) && !checkIfPlayerHasValidMoves(blackQueen4)) {
				logs.add(0, "White player won !");
				gameFinished = true;
			}
			
			// If single player mode, next move is performed by the AI
			if(gameMode.equals("singleplayer"))
				computerTurn();
		}
	}
	
	@FXML
	private void exitGameButtonHandler() throws IOException {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setContentText("Do you really want to quit this game ?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root,600,400);
			stage.setScene(scene);
			
			HomeController controller = loader.getController();
			controller.setStage(stage);
		}
	}
	
	/**
	 * Method to retrieve the board square based on the row and column of the grid pane
	 * @param row Row index
	 * @param column Column index
	 * @return Node
	 */
	private Node getBoardSquare (int row, int column) {
	    return gpBoard.getChildren().get(row*11 + column);
	}
	
	private void highlightPossibleMoves(VBox square) {
		resetBoardSquareColors();
		
		int row = GridPane.getRowIndex(square);
		int column = GridPane.getColumnIndex(square);
		
		// Highlight diagonals
		int j = column + 1;
		for(int i = row + 1; i < 10; i++) {
			// Stop loop if j exceeds board's column boundary
			if(j > 10)
				break;
			
			VBox squareToHighlight = (VBox) getBoardSquare(i, j);
			
			// If square is not empty empty exit (No more possible moves in this direction)
			if(squareToHighlight.getChildren().size() > 0)
				break;
			
			squareToHighlight.setStyle("-fx-background-color: rgba(255, 0, 0, 0.1)");
			
			j++;
		}
		j = column - 1;
		for(int i = row - 1; i >= 0; i--) {
			// Stop loop if j exceeds board's column boundary
			if(j < 0)
				break;
			
			VBox squareToHighlight = (VBox) getBoardSquare(i, j);
			
			// If square is not empty empty exit (No more possible moves in this direction)
			if(squareToHighlight.getChildren().size() > 0)
				break;
			
			squareToHighlight.setStyle("-fx-background-color: rgba(255, 0, 0, 0.1)");
			
			j--;
		}
		j = column - 1;
		for(int i = row + 1; i < 10; i++) {
			// Stop loop if j exceeds board's column boundary
			if(j < 0)
				break;
			
			VBox squareToHighlight = (VBox) getBoardSquare(i, j);
			
			// If square is not empty empty exit (No more possible moves in this direction)
			if(squareToHighlight.getChildren().size() > 0)
				break;
			
			squareToHighlight.setStyle("-fx-background-color: rgba(255, 0, 0, 0.1)");
			
			j--;
		}
		j = column + 1;
		for(int i = row - 1; i >= 0; i--) {
			// Stop loop if j exceeds board's column boundary
			if(j > 10)
				break;
			
			VBox squareToHighlight = (VBox) getBoardSquare(i, j);
			
			// If square is not empty empty exit (No more possible moves in this direction)
			if(squareToHighlight.getChildren().size() > 0)
				break;
			
			squareToHighlight.setStyle("-fx-background-color: rgba(255, 0, 0, 0.1)");
			
			j++;
		}
		
		// Highlight horizontals
		for(int i = column + 1; i <= 10; i++) {
			VBox squareToHighlight = (VBox) getBoardSquare(row, i);
			
			// If square is not empty empty exit (No more possible moves in this direction)
			if(squareToHighlight.getChildren().size() > 0)
				break;
			
			squareToHighlight.setStyle("-fx-background-color: rgba(255, 0, 0, 0.1)");
		}
		for(int i = column - 1; i > 0; i--) {
			VBox squareToHighlight = (VBox) getBoardSquare(row, i);
			
			// If square is not empty empty exit (No more possible moves in this direction)
			if(squareToHighlight.getChildren().size() > 0)
				break;
			
			squareToHighlight.setStyle("-fx-background-color: rgba(255, 0, 0, 0.1)");
		}
		
		// Highlight verticals
		for(int i = row + 1; i < 10; i++) {
			VBox squareToHighlight = (VBox) getBoardSquare(i, column);
			
			// If square is not empty empty exit (No more possible moves in this direction)
			if(squareToHighlight.getChildren().size() > 0)
				break;
			
			squareToHighlight.setStyle("-fx-background-color: rgba(255, 0, 0, 0.1)");
		}
		for(int i = row - 1; i >= 0; i--) {
			VBox squareToHighlight = (VBox) getBoardSquare(i, column);
			
			// If square is not empty empty exit (No more possible moves in this direction)
			if(squareToHighlight.getChildren().size() > 0)
				break;
			
			squareToHighlight.setStyle("-fx-background-color: rgba(255, 0, 0, 0.1)");
		}
	}
	
	private void resetBoardSquareColors() {
		for(int i = 0; i < 10; i++) {
			for(int j = 1; j < 11; j++) {
				VBox square = (VBox) getBoardSquare(i, j);
				
				if(((i * 11 + j) & 1) == 0)
					square.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5)");
				else
					square.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5)");
			}
		}
	}
	
	/**
	 * Checks the validity of a move based on if the target is highlighted or not
	 * @param target Target square
	 * @return true if valid, false otherwise
	 */
	private boolean checkValidMove(VBox target) {
		if(target.getBackground().getFills().get(0).getFill().toString().equals("0xff00001a"))
			return true;
		
		return false;
	}
	
	/**
	 * Checks if given queen has valid moves
	 * Used to determine if player lost the game
	 * @param queen
	 * @return true if queen has valid moves, false otherwise
	 */
	private boolean checkIfPlayerHasValidMoves(ImageView queen) {
		VBox square = (VBox) queen.getParent();
		int row = GridPane.getRowIndex(square);
		int column = GridPane.getColumnIndex(square);
		int possibleMoves = 0;
		
		// Check for each adjacent square if there is at least one empty
		try {
			VBox square1 = (VBox) getBoardSquare(row + 1, column);
			
			if(square1.getChildren().size() == 0)
				possibleMoves++;
		} catch(Exception e) {}
		
		try {
			VBox square2 = (VBox) getBoardSquare(row - 1, column);
			
			if(square2.getChildren().size() == 0)
				possibleMoves++;
		} catch(Exception e) {}
	
		try {
			VBox square3 = (VBox) getBoardSquare(row, column + 1);
			
			if(square3.getChildren().size() == 0)
				possibleMoves++;
		} catch(Exception e) {}
		
		try {
			VBox square4 = (VBox) getBoardSquare(row, column - 1);
			
			if(square4.getChildren().size() == 0)
				possibleMoves++;
		} catch(Exception e) {}
		
		try {
			VBox square5 = (VBox) getBoardSquare(row - 1, column + 1);
			
			if(square5.getChildren().size() == 0)
				possibleMoves++;
		} catch(Exception e) {}
		
		try {
			VBox square6 = (VBox) getBoardSquare(row - 1, column - 1);
			
			if(square6.getChildren().size() == 0)
				possibleMoves++;
		} catch(Exception e) {}
		
		try {
			VBox square7 = (VBox) getBoardSquare(row + 1, column - 1);
			
			if(square7.getChildren().size() == 0)
				possibleMoves++;
		} catch(Exception e) {}
		
		try {
			VBox square8 = (VBox) getBoardSquare(row + 1, column + 1);
			
			if(square8.getChildren().size() == 0)
				possibleMoves++;
		} catch(Exception e) {}
		
		if(possibleMoves > 0)
			return true;
		
		return false;
	}
	
	private void computerTurn() {
		// Check if next player has valid moves
		if(!checkIfPlayerHasValidMoves(blackQueen1) && !checkIfPlayerHasValidMoves(blackQueen2) && !checkIfPlayerHasValidMoves(blackQueen3) && !checkIfPlayerHasValidMoves(blackQueen4)) {
			logs.add(0, "White player won !");
			gameFinished = true;
			return;
		}
				
		Random rand = new Random();

		ImageView queen;
		while(true) {
			int queenNb = rand.nextInt(4) + 1;
			if(queenNb == 1 && checkIfPlayerHasValidMoves(blackQueen1)) {
				queen = blackQueen1;
				break;
			} else if(queenNb == 2 && checkIfPlayerHasValidMoves(blackQueen2)) {
				queen = blackQueen2;
				break;
			} else if(queenNb == 3 && checkIfPlayerHasValidMoves(blackQueen3)) {
				queen = blackQueen3;
				break;
			} else if(queenNb == 4 && checkIfPlayerHasValidMoves(blackQueen4)) {
				queen = blackQueen4;
				break;
			}
		}
		
		VBox square = null;
		boolean validMove = false;
		
		while(!validMove) {
			int row = GridPane.getRowIndex(queen.getParent());
			int column = GridPane.getColumnIndex(queen.getParent());
			
			int nextRow = rand.nextInt(10) + 1;
			int nextColumn = rand.nextInt(10) + 1;
			
			square = (VBox) getBoardSquare(nextRow, nextColumn);
			
			// Diagonals
			int j = column + 1;
			for(int i = row + 1; i < 10; i++) {
				// Stop loop if j exceeds board's column boundary
				if(j > 10)
					break;
				
				VBox squareToTest = (VBox) getBoardSquare(i, j);
				
				// If square is not empty empty exit (No more possible moves in this direction)
				if(squareToTest.getChildren().size() > 0)
					break;
				
				if(i == nextRow && j == nextColumn) {
					validMove = true;
					break;
				}
				
				j++;
			}
			j = column - 1;
			for(int i = row - 1; i >= 0; i--) {
				// Stop loop if j exceeds board's column boundary
				if(j < 0)
					break;
				
				VBox squareToTest = (VBox) getBoardSquare(i, j);
				
				// If square is not empty empty exit (No more possible moves in this direction)
				if(squareToTest.getChildren().size() > 0)
					break;
				
				if(i == nextRow && j == nextColumn) {
					validMove = true;
					break;
				}
				
				j--;
			}
			j = column - 1;
			for(int i = row + 1; i < 10; i++) {
				// Stop loop if j exceeds board's column boundary
				if(j < 0)
					break;
				
				VBox squareToTest = (VBox) getBoardSquare(i, j);
				
				// If square is not empty empty exit (No more possible moves in this direction)
				if(squareToTest.getChildren().size() > 0)
					break;
				
				if(i == nextRow && j == nextColumn) {
					validMove = true;
					break;
				}
				
				j--;
			}
			j = column + 1;
			for(int i = row - 1; i >= 0; i--) {
				// Stop loop if j exceeds board's column boundary
				if(j > 10)
					break;
				
				VBox squareToTest = (VBox) getBoardSquare(i, j);
				
				// If square is not empty empty exit (No more possible moves in this direction)
				if(squareToTest.getChildren().size() > 0)
					break;
				
				if(i == nextRow && j == nextColumn) {
					validMove = true;
					break;
				}
				
				j++;
			}
			
			// Horizontals
			for(int i = column + 1; i <= 10; i++) {
				VBox squareToTest = (VBox) getBoardSquare(row, i);
				
				// If square is not empty empty exit (No more possible moves in this direction)
				if(squareToTest.getChildren().size() > 0)
					break;
				
				if(i == nextColumn && nextRow == row) {
					validMove = true;
					break;
				}
			}
			for(int i = column - 1; i > 0; i--) {
				VBox squareToTest = (VBox) getBoardSquare(row, i);
				
				// If square is not empty empty exit (No more possible moves in this direction)
				if(squareToTest.getChildren().size() > 0)
					break;
				
				if(i == nextColumn && nextRow == row) {
					validMove = true;
					break;
				}
			}
			
			// Verticals
			for(int i = row + 1; i < 10; i++) {
				VBox squareToTest = (VBox) getBoardSquare(i, column);
				
				// If square is not empty empty exit (No more possible moves in this direction)
				if(squareToTest.getChildren().size() > 0)
					break;
				
				if(i == nextRow && nextColumn == column) {
					validMove = true;
					break;
				}
			}
			for(int i = row - 1; i >= 0; i--) {
				VBox squareToTest = (VBox) getBoardSquare(i, column);
				
				// If square is not empty empty exit (No more possible moves in this direction)
				if(squareToTest.getChildren().size() > 0)
					break;
				
				if(i == nextRow && nextColumn == column) {
					validMove = true;
					break;
				}
			}
		}

		// Move queen to new square
		VBox squareToBeCleared = (VBox) queen.getParent();
		squareToBeCleared.getChildren().clear();
		square.getChildren().add(queen);
		
		// Save move in logs
		logs.add(0, "Move " + columnsNotation[GridPane.getColumnIndex(squareToBeCleared) - 1] + rowsNotation[GridPane.getRowIndex(squareToBeCleared)] + " to " + columnsNotation[GridPane.getColumnIndex(square) - 1] + rowsNotation[GridPane.getRowIndex(square)]);
		
		
		// Throw spear
		validMove = false;
		while(!validMove) {
			int row = GridPane.getRowIndex(queen.getParent());
			int column = GridPane.getColumnIndex(queen.getParent());
			
			int nextRow = rand.nextInt(10) + 1;
			int nextColumn = rand.nextInt(10) + 1;
			
			square = (VBox) getBoardSquare(nextRow, nextColumn);
			
			// Diagonals
			int j = column + 1;
			for(int i = row + 1; i < 10; i++) {
				// Stop loop if j exceeds board's column boundary
				if(j > 10)
					break;
				
				VBox squareToTest = (VBox) getBoardSquare(i, j);
				
				// If square is not empty empty exit (No more possible moves in this direction)
				if(squareToTest.getChildren().size() > 0)
					break;
				
				if(i == nextRow && j == nextColumn) {
					validMove = true;
					break;
				}
				
				j++;
			}
			j = column - 1;
			for(int i = row - 1; i >= 0; i--) {
				// Stop loop if j exceeds board's column boundary
				if(j < 0)
					break;
				
				VBox squareToTest = (VBox) getBoardSquare(i, j);
				
				// If square is not empty empty exit (No more possible moves in this direction)
				if(squareToTest.getChildren().size() > 0)
					break;
				
				if(i == nextRow && j == nextColumn) {
					validMove = true;
					break;
				}
				
				j--;
			}
			j = column - 1;
			for(int i = row + 1; i < 10; i++) {
				// Stop loop if j exceeds board's column boundary
				if(j < 0)
					break;
				
				VBox squareToTest = (VBox) getBoardSquare(i, j);
				
				// If square is not empty empty exit (No more possible moves in this direction)
				if(squareToTest.getChildren().size() > 0)
					break;
				
				if(i == nextRow && j == nextColumn) {
					validMove = true;
					break;
				}
				
				j--;
			}
			j = column + 1;
			for(int i = row - 1; i >= 0; i--) {
				// Stop loop if j exceeds board's column boundary
				if(j > 10)
					break;
				
				VBox squareToTest = (VBox) getBoardSquare(i, j);
				
				// If square is not empty empty exit (No more possible moves in this direction)
				if(squareToTest.getChildren().size() > 0)
					break;
				
				if(i == nextRow && j == nextColumn) {
					validMove = true;
					break;
				}
				
				j++;
			}
			
			// Horizontals
			for(int i = column + 1; i <= 10; i++) {
				VBox squareToTest = (VBox) getBoardSquare(row, i);
				
				// If square is not empty empty exit (No more possible moves in this direction)
				if(squareToTest.getChildren().size() > 0)
					break;
				
				if(i == nextColumn && nextRow == row) {
					validMove = true;
					break;
				}
			}
			for(int i = column - 1; i > 0; i--) {
				VBox squareToTest = (VBox) getBoardSquare(row, i);
				
				// If square is not empty empty exit (No more possible moves in this direction)
				if(squareToTest.getChildren().size() > 0)
					break;
				
				if(i == nextColumn && nextRow == row) {
					validMove = true;
					break;
				}
			}
			
			// Verticals
			for(int i = row + 1; i < 10; i++) {
				VBox squareToTest = (VBox) getBoardSquare(i, column);
				
				// If square is not empty empty exit (No more possible moves in this direction)
				if(squareToTest.getChildren().size() > 0)
					break;
				
				if(i == nextRow && nextColumn == column) {
					validMove = true;
					break;
				}
			}
			for(int i = row - 1; i >= 0; i--) {
				VBox squareToTest = (VBox) getBoardSquare(i, column);
				
				// If square is not empty empty exit (No more possible moves in this direction)
				if(squareToTest.getChildren().size() > 0)
					break;
				
				if(i == nextRow && nextColumn == column) {
					validMove = true;
					break;
				}
			}
		}
		square.getChildren().add(new Text("X"));
		
		// Reset
		turn = "white";
					
		// Save move in logs
		logs.add(0, "Spear on " + columnsNotation[GridPane.getColumnIndex(square) - 1] + rowsNotation[GridPane.getRowIndex(square)]);
		logs.add(0, (turn.equals("white") ? "White" : "Black") + " player turn");
	}
}