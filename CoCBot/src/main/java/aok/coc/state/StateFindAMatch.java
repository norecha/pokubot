package aok.coc.state;

import java.util.logging.Logger;

import aok.coc.util.Clickable;
import aok.coc.util.RobotUtils;

public class StateFindAMatch implements State {
	private static final Logger				logger		= Logger.getLogger(StateFindAMatch.class.getName());

	private static final StateFindAMatch	instance	= new StateFindAMatch();

	public static StateFindAMatch instance() {
		return instance;
	}

	private StateFindAMatch() {
	}

	@Override
	public void handle(Context context) {
		logger.info("StateFindAMatch");
		if (RobotUtils.isClickableActive(Clickable.BUTTON_FIND_A_MATCH)) {
			try {
				RobotUtils.leftClick(Clickable.BUTTON_FIND_A_MATCH, 200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}

			context.setState(StateAttack.instance());
		} else {
			context.setState(StateIdle.instance());
		}
	}

}
