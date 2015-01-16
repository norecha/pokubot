package aok.coc.controller;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import aok.coc.launcher.BotLauncher;
import aok.coc.util.ConfigUtils;

public class MainWindowController {

	@FXML
	private TextField			goldField;
	@FXML
	private TextField			elixirField;
	@FXML
	private TextField			deField;
	@FXML
	private TextField			maxThField;
	@FXML
	private CheckBox			isMatchAllConditionsCheckBox;
	@FXML
	private Button				startButton;
	@FXML
	private Button				stopButton;
	@FXML
	private TextArea			textArea;
	@FXML
	private GridPane			configGridPane;

	private static final Logger	logger			= Logger.getLogger(MainWindowController.class.getName());

	private BotLauncher			botLauncher		= null;
	private Service<Void>		setupService	= null;
	private Service<Void>		runnerService	= null;
	private boolean				isSetupDone		= false;

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

		for (Handler h : Logger.getLogger("").getHandlers()) {
			if (h instanceof UILogHandler) {
				((UILogHandler) h).setTextArea(textArea);
			}
		}

		botLauncher = new BotLauncher();

		setupService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						try {
							botLauncher.setup();
							return null;
						} catch (Exception e) {
							logger.log(Level.SEVERE, e.getMessage(), e);
							throw e;
						}
					}
				};
			}
		};
		setupService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				updateConfigGridPane();
				isSetupDone = true;
				logger.info("Setup is succesful.");
			}
		});

		setupService.setOnFailed(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				isSetupDone = false;
				setupService.reset();
				logger.info("Setup is failed.");
			}
		});

		setupService.setOnCancelled(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				isSetupDone = false;
				setupService.reset();
				logger.info("Setup is cancelled.");
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
							return null;
						} catch (Exception e) {
							logger.log(Level.SEVERE, e.getMessage(), e);
							throw e;
						}
					}
				};
			}
		};

		runnerService.setOnCancelled(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				logger.info("runner is cancelled.");
			}
		});

		runnerService.setOnFailed(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				logger.info("runner is failed.");
			}
		});
	}

	@FXML
	public void handleStartButtonAction() {
		if (!isSetupDone && setupService.getState() == State.READY) {
			setupService.start();
		}

		if (isSetupDone && runnerService.getState() == State.READY) {
			runnerService.start();
		}
	}

	private void updateConfigGridPane() {
		goldField.setText(ConfigUtils.instance().getGoldThreshold() + "");
		elixirField.setText(ConfigUtils.instance().getElixirThreshold() + "");
		deField.setText(ConfigUtils.instance().getDarkElixirThreshold() + "");
		maxThField.setText(ConfigUtils.instance().getMaxThThreshold() + "");

		isMatchAllConditionsCheckBox.setSelected(ConfigUtils.instance().isMatchAllConditions());

		configGridPane.setVisible(true);
	}

	@FXML
	public void handleStopButtonAction() {
		if (setupService.isRunning()) {
			setupService.cancel();
			setupService.reset();
		}
		if (runnerService.isRunning()) {
			runnerService.cancel();
			runnerService.reset();
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
