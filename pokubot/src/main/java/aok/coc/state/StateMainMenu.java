package aok.coc.state;

import java.util.logging.Logger;

import aok.coc.exception.BotConfigurationException;
import aok.coc.util.RobotUtils;
import aok.coc.util.coords.Clickable;

public class StateMainMenu implements State {
	private static final Logger			logger		= Logger.getLogger(StateMainMenu.class.getName());

	private static final StateMainMenu	instance	= new StateMainMenu();

	private StateMainMenu() {
	}

	@Override
	public void handle(Context context) throws BotConfigurationException, InterruptedException {
		logger.info("StateMainMenu");
		if (Thread.interrupted()) {
			throw new InterruptedException("StateMainMenu is interrupted.");
		}
		RobotUtils.zoomUp();

		RobotUtils.sleepRandom(350);
		RobotUtils.leftClick(Clickable.UNIT_FIRST_RAX, 500);

		boolean maxThActive = RobotUtils.isClickableActive(Clickable.BUTTON_RAX_MAX_TRAIN);
		boolean lowThActive = RobotUtils.isClickableActive(Clickable.BUTTON_RAX_TRAIN);
		if (!maxThActive  && !lowThActive) {
			// maybe rax was already open and we closed it back. try one more time
			RobotUtils.leftClick(Clickable.UNIT_FIRST_RAX, 500);

			maxThActive = RobotUtils.isClickableActive(Clickable.BUTTON_RAX_MAX_TRAIN);
			lowThActive = RobotUtils.isClickableActive(Clickable.BUTTON_RAX_TRAIN);
			// if still not active, throw exception
			if (!maxThActive  && !lowThActive) {
				logger.severe("Unable to locate barracks.");
				throw new BotConfigurationException("Barracks location is not correct.");
			}
		}
		
		if (maxThActive) {
			RobotUtils.leftClick(Clickable.BUTTON_RAX_MAX_TRAIN, 500);
		} else {
			RobotUtils.leftClick(Clickable.BUTTON_RAX_TRAIN, 500);
		}

		// camp is full
		if (RobotUtils.isClickableActive(Clickable.BUTTON_RAX_FULL)) {
			logger.info("Camp is full");
			RobotUtils.leftClick(Clickable.BUTTON_RAX_CLOSE, 200);

			RobotUtils.leftClick(Clickable.BUTTON_ATTACK, 1000);

			context.setState(StateFindAMatch.instance());
		} else {
			context.setState(StateTrainTroops.instance());
		}
		Thread.sleep(500 + RobotUtils.random.nextInt(500));
	}

	public static StateMainMenu instance() {
		return instance;
	}

}
