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

		Thread.sleep(100 + RobotUtils.random.nextInt(100));
		RobotUtils.leftClick(Clickable.UNIT_FIRST_RAX, 500);

		if (!RobotUtils.isClickableActive(Clickable.BUTTON_RAX_TRAIN)) {
			// maybe rax was already open and we closed it back. try one more time
			RobotUtils.leftClick(Clickable.UNIT_FIRST_RAX, 500);

			// if still not active, throw exception
			if (!RobotUtils.isClickableActive(Clickable.BUTTON_RAX_TRAIN)) {
				logger.severe("Unable to locate barracks.");
				throw new BotConfigurationException("Barracks location is not correct.");
			}
		}
		RobotUtils.leftClick(Clickable.BUTTON_RAX_TRAIN, 500);

		// camp is full
		if (RobotUtils.isClickableActive(Clickable.BUTTON_RAX_FULL)) {
			System.out.println("Camp is full");
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
