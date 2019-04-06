package com.joeyhelou.amazons;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomeController {
	private Stage stage;
	
	@FXML
	private void playerVPlayerButtonHandler() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root,600,400);
		stage.setScene(scene);
		
		GameController controller = loader.getController();
		controller.setStage(stage);
		controller.setGameMode("multiplayer");
	}
	
	@FXML
	private void playerVComputerButtonHandler() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root,600,400);
		stage.setScene(scene);
		
		GameController controller = loader.getController();
		controller.setStage(stage);
		controller.setGameMode("singleplayer");
	}
	
	@FXML
	private void exitButtonHandler() {
		System.exit(1);
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
