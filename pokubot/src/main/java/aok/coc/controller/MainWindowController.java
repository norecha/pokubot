package aok.coc.controller;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import aok.coc.launcher.BotLauncher;
import aok.coc.util.ConfigUtils;

public class MainWindowController {

	@FXML
	private TextField	goldField;
	@FXML
	private TextField	elixirField;
	@FXML
	private TextField	deField;
	@FXML
	private TextField	maxThField;
	@FXML
	private CheckBox	isMatchAllConditionsCheckBox;
	@FXML
	Button				startButton;
	@FXML
	Button				stopButton;
	@FXML GridPane configGridPane;

	private	BotLauncher	botLauncher = null;
	Service<Void> setupService = null;
	Service<Void> runnerService = null;
	
	@FXML
	private void initialize() {
		ChangeListener<String> intFieldListener = new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				try {
					if (!newValue.isEmpty()) {
						Integer.parseInt(newValue);
					}
				} catch (NumberFormatException e) {
					((TextField) ((StringProperty) observable).getBean()).setText(oldValue);
				}
			}
		};
		goldField.textProperty().addListener(intFieldListener);
		elixirField.textProperty().addListener(intFieldListener);
		deField.textProperty().addListener(intFieldListener);
		maxThField.textProperty().addListener(intFieldListener);
		
		botLauncher = new BotLauncher();
		
		setupService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						try {
							botLauncher.setup();
						} catch (Exception e) {
							setException(e);
							e.printStackTrace();
						}
						return null;
					}
				};
			}
		};
		setupService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			
			@Override
			public void handle(WorkerStateEvent event) {
				updateConfigGridPane();
			}
		});
		
		runnerService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						try {
							botLauncher.start();
						} catch (Exception e) {
							setException(e);
							e.printStackTrace();
						}
						return null;
					}
				};
			}
		};
		
		runnerService.setOnCancelled(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				// TODO Auto-generated method stub
				System.out.println("cancelled " + event.getSource().getException());
				event.getSource().getException().printStackTrace();
			}
		});
		

		runnerService.setOnFailed(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				// TODO Auto-generated method stub
				System.out.println("failed ");
				event.getSource().getException().printStackTrace();
			}
		});
	}

	@FXML
	public void handleStartButtonAction()  {
		setupService.start();
	}
	
	private void updateConfigGridPane() {
		goldField.setText(ConfigUtils.instance().getGoldThreshold() + "");
		elixirField.setText(ConfigUtils.instance().getElixirThreshold() + "");
		deField.setText(ConfigUtils.instance().getDarkElixirThreshold() + "");
		maxThField.setText(ConfigUtils.instance().getMaxThThreshold() + "");
		
		isMatchAllConditionsCheckBox.setSelected(ConfigUtils.instance().isMatchAllConditions());
		
		configGridPane.setVisible(true);
		
		runnerService.start();
	}

	@FXML
	public void handleStopButtonAction() {
		System.out.println("stop clicked");
		if (setupService.isRunning()) {
			System.out.println("setup runninginigin");
			setupService.cancel();
		}
		if (runnerService.isRunning()) {
			System.out.println("runner runninginigin");
			runnerService.cancel();
		}
	}

	@FXML
	public void handleSaveButtonAction() {
		if (!goldField.getText().isEmpty()) {
			ConfigUtils.instance().setGoldThreshold(Integer.parseInt(goldField.getText()));
		}

		if (!elixirField.getText().isEmpty()) {
			ConfigUtils.instance().setElixirThreshold(Integer.parseInt(elixirField.getText()));
		}

		if (!deField.getText().isEmpty()) {
			ConfigUtils.instance().setDarkElixirThreshold(Integer.parseInt(deField.getText()));
		}

		if (!maxThField.getText().isEmpty()) {
			ConfigUtils.instance().setMaxThThreshold(Integer.parseInt(maxThField.getText()));
		}

		ConfigUtils.instance().setMatchAllConditions(isMatchAllConditionsCheckBox.isSelected());

		ConfigUtils.instance().save();
	}

}
