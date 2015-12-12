package aok.coc.state;

import aok.coc.exception.BotException;
import aok.coc.util.RobotUtils;
import aok.coc.util.coords.Clickable;

import java.util.logging.Logger;

public class StateMainMenu implements State {
	private static final Logger			logger		= Logger.getLogger(StateMainMenu.class.getName());

	private static final StateMainMenu	instance	= new StateMainMenu();

	private StateMainMenu() {
	}

	@Override
	public void handle(Context context) throws BotException, InterruptedException {
		logger.info("StateMainMenu");
		if (Thread.interrupted()) {
			throw new InterruptedException("StateMainMenu is interrupted.");
		}
		RobotUtils.zoomOut();

		/**
		 * 1 zoom is equal to ~3 zoom out.
		 * With TH11 update, map size is increased and you can zoom out even further.
		 * To make empty collector detection work, we need to zoom-in little bit to
		 * get a similar resolution.
		 */
		RobotUtils.zoomIn(1);
		RobotUtils.zoomOut(1);

		RobotUtils.sleepRandom(350);
		RobotUtils.leftClick(Clickable.BUTTON_ARMY_OVERVIEW, 500);
		
		// camp is full
		if (RobotUtils.isClickableActive(Clickable.BUTTON_ARMY_OVERVIEW_READY)) {
			logger.info("Camp is full");
			RobotUtils.leftClick(Clickable.BUTTON_ARMY_OVERVIEW_CLOSE, 200);

			RobotUtils.leftClick(Clickable.BUTTON_ATTACK, 1000);

			context.setState(StateFindAMatch.instance());
		} else {
			RobotUtils.leftClick(Clickable.BUTTON_ARMY_OVERVIEW_NEXT, 200);
			context.setState(StateTrainTroops.instance());
		}
		Thread.sleep(500 + RobotUtils.random.nextInt(500));
	}

	public static StateMainMenu instance() {
		return instance;
	}

}
