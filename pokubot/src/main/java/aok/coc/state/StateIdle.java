package aok.coc.state;

import java.util.logging.Logger;

import aok.coc.exception.BotException;
import aok.coc.util.RobotUtils;
import aok.coc.util.coords.Clickable;

public class StateIdle implements State {
	private static final Logger logger = Logger.getLogger(StateIdle.class.getName());

	private static final StateIdle instance = new StateIdle();

	boolean reloading = false;

	private StateIdle() {
	}

	@Override
	public void handle(Context context) throws InterruptedException, BotException {
		State nextState = null;
		while (true) {
			logger.info("StateIdle");
			if (Thread.interrupted()) {
				throw new InterruptedException("StateIdle is interrupted.");
			}

			if (reloading) {
				logger.info("reloading...");
				Thread.sleep(2000);
				continue;
			}

			if (RobotUtils.isClickableActive(Clickable.BUTTON_WAS_ATTACKED_HEADLINE)
					|| RobotUtils.isClickableActive(Clickable.BUTTON_WAS_ATTACKED_OKAY)) {
				logger.info("Was attacked.");
				RobotUtils.leftClick(Clickable.BUTTON_WAS_ATTACKED_OKAY, 250);
			} else if (RobotUtils.isClickableActive(Clickable.BUTTON_ATTACK_NO_STAR, 0xF0E300, 0xF0EA00)
					|| RobotUtils.isClickableActive(Clickable.BUTTON_ATTACK_STAR, 0xF0E300, 0xF0EA00)) {
				nextState = StateMainMenu.instance();
				break;
			} else if (RobotUtils.isClickableActive(Clickable.BUTTON_NEXT)) {
				nextState = StateAttack.instance();
				break;
			} else if (RobotUtils.isClickableActive(Clickable.BUTTON_FIND_A_MATCH_1)
					|| RobotUtils.isClickableActive(Clickable.BUTTON_FIND_A_MATCH_2)) {
				nextState = StateFindAMatch.instance();
				break;
			} else {
				// Close any misc. notification
				RobotUtils.leftClick(Clickable.CLOSE_NOTIFICATION, 500);
			}

			Thread.sleep(1000);
		}

		context.setState(nextState);
	}

	public static StateIdle instance() {
		return instance;
	}

	public boolean isReloading() {
		return reloading;
	}

	public void setReloading(boolean reloading) {
		this.reloading = reloading;
	}

}
