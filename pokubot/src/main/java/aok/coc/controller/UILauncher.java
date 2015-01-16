package aok.coc.controller;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class UILauncher extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
		Button btn = new Button();
		btn.setText("Say 'Hello World'");
		btn.setOnAction(e -> System.out.println(e.getSource()));
		//		btn.setOnAction(new EventHandler<ActionEvent>() {
		//
		//			@Override
		//			public void handle(ActionEvent event) {
		//				System.out.println("Hello World!");
		//			}
		//		});

		StackPane root = new StackPane();
		root.getChildren().add(btn);

		primaryStage.setTitle("PokuBot");
		primaryStage.show();

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(UILauncher.class.getResource("/fxml/MainWindow.fxml"));
		AnchorPane scene = loader.load();
		
		primaryStage.setScene(new Scene(scene));
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}