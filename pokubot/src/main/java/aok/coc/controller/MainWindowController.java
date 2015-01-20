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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import aok.coc.launcher.BotLauncher;
import aok.coc.util.ConfigUtils;
import aok.coc.util.coords.Clickable;

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
	@FXML
	private ComboBox<String>	autoAttackComboBox;
	@FXML
	private CheckBox			detectEmptyCollectorsCheckBox;
	@FXML
	private CheckBox			playSoundCheckBox;
	@FXML
	private Label				versionLabel;
	@FXML
	private ComboBox<String>	rax1ComboBox;
	@FXML
	private ComboBox<String>	rax2ComboBox;
	@FXML
	private ComboBox<String>	rax3ComboBox;
	@FXML
	private ComboBox<String>	rax4ComboBox;

	private static final Logger	logger			= Logger.getLogger(MainWindowController.class.getName());

	private BotLauncher			botLauncher		= null;
	private Service<Void>		setupService	= null;
	private Service<Void>		runnerService	= null;
	private boolean				isSetupDone		= false;

	@FXML
	private void initialize() {
		for (Handler h : Logger.getLogger("").getHandlers()) {
			if (h instanceof UILogHandler) {
				((UILogHandler) h).setTextArea(textArea);
			}
		}

		botLauncher = new BotLauncher();

		initializeLabels();
		initializeTextFields();
		initializeSetupService();
		initializeRunnerService();
	}

	private void initializeLabels() {
		String version = getClass().getPackage().getImplementationVersion();
		if (version != null) {
			versionLabel.setText("PokuBot v" + version);
		}
	}

	private void initializeRunnerService() {
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
							logger.log(Level.SEVERE, "runner: " + e.getMessage(), e);
							throw e;
						}
					}
				};
			}
		};

		runnerService.setOnCancelled(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				logger.warning("runner is cancelled.");
				runnerService.reset();
			}
		});

		runnerService.setOnFailed(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				logger.severe("runner is failed.");
				runnerService.reset();
			}
		});
	}

	private void initializeSetupService() {
		setupService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						try {
							botLauncher.tearDown();
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
				initializeComboBox();
				updateConfigGridPane();
				isSetupDone = true;
				logger.info("Setup is successful.");
				logger.info("Click start to run.");
			}
		});

		setupService.setOnFailed(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				isSetupDone = false;
				setupService.reset();
				logger.severe("Setup is failed.");
			}
		});

		setupService.setOnCancelled(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				isSetupDone = false;
				setupService.reset();
				logger.warning("Setup is cancelled.");
			}
		});
	}

	private void initializeTextFields() {
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
	}

	private void initializeComboBox() {
		autoAttackComboBox.getItems().addAll(
			ConfigUtils.instance().getAttackStrategies()
			);
		autoAttackComboBox.setValue(autoAttackComboBox.getItems().get(0));

		Clickable[] availableTroops = ConfigUtils.instance().getAvailableTroops();
		String[] troops = new String[availableTroops.length];
		for (int i = 0; i < availableTroops.length; i++) {
			Clickable c = availableTroops[i];
			troops[i] = c.getDescription();
		}

		rax1ComboBox.getItems().addAll(
			troops
			);
		rax2ComboBox.getItems().addAll(
			troops
			);
		rax3ComboBox.getItems().addAll(
			troops
			);
		rax4ComboBox.getItems().addAll(
			troops
			);
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
		detectEmptyCollectorsCheckBox.setSelected(ConfigUtils.instance().isDetectEmptyCollectors());
		playSoundCheckBox.setSelected(ConfigUtils.instance().isPlaySound());
		autoAttackComboBox.getSelectionModel().select(ConfigUtils.instance().getAttackStrategy().getClass().getSimpleName());
		rax1ComboBox.getSelectionModel().select(ConfigUtils.instance().getRaxInfo()[0].getDescription());
		rax2ComboBox.getSelectionModel().select(ConfigUtils.instance().getRaxInfo()[1].getDescription());
		rax3ComboBox.getSelectionModel().select(ConfigUtils.instance().getRaxInfo()[2].getDescription());
		rax4ComboBox.getSelectionModel().select(ConfigUtils.instance().getRaxInfo()[3].getDescription());

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
		ConfigUtils.instance().setDetectEmptyCollectors(detectEmptyCollectorsCheckBox.isSelected());
		ConfigUtils.instance().setPlaySound(playSoundCheckBox.isSelected());
		ConfigUtils.instance().setAttackStrategy(autoAttackComboBox.getValue());
		ConfigUtils.instance().getRaxInfo()[0] = Clickable.fromDescription(rax1ComboBox.getValue());
		ConfigUtils.instance().getRaxInfo()[1] = Clickable.fromDescription(rax2ComboBox.getValue());
		ConfigUtils.instance().getRaxInfo()[2] = Clickable.fromDescription(rax3ComboBox.getValue());
		ConfigUtils.instance().getRaxInfo()[3] = Clickable.fromDescription(rax4ComboBox.getValue());

		ConfigUtils.instance().save();
	}

}
