package aok.coc.state;

import java.util.logging.Logger;

import aok.coc.util.RobotUtils;
import aok.coc.util.coords.Clickable;

public class StateFindAMatch implements State {
	private static final Logger				logger		= Logger.getLogger(StateFindAMatch.class.getName());

	private static final StateFindAMatch	instance	= new StateFindAMatch();

	public static StateFindAMatch instance() {
		return instance;
	}

	private StateFindAMatch() {
	}

	@Override
	public void handle(Context context) throws InterruptedException {
		logger.info("StateFindAMatch");
		if (Thread.interrupted()) {
			throw new InterruptedException("StateFindAMatch is interrupted.");
		}
		if (RobotUtils.isClickableActive(Clickable.BUTTON_FIND_A_MATCH)) {
			RobotUtils.leftClick(Clickable.BUTTON_FIND_A_MATCH, 300);
			RobotUtils.leftClick(Clickable.BUTTON_SHIELD_DISABLE, 100);
			RobotUtils.sleepTillClickableIsActive(Clickable.BUTTON_NEXT);

			context.setState(StateAttack.instance());
		} else {
			context.setState(StateIdle.instance());
		}
	}

}
