package aok.coc.state;

import java.util.logging.Logger;

import aok.coc.util.Clickable;
import aok.coc.util.RobotUtils;

public class StateIdle implements State {
	private static final Logger		logger		= Logger.getLogger(StateIdle.class.getName());

	private static final StateIdle	instance	= new StateIdle();

	private StateIdle() {
	}

	@Override
	public void handle(Context context) {
		State nextState = null;
		while (true) {
			try {
				logger.info("StateIdle");
				if (Thread.interrupted()) {
					throw new InterruptedException("StateIdle is interrupted.");
				}
				if (RobotUtils.isClickableActive(Clickable.BUTTON_ATTACK)) {
					nextState = StateMainMenu.instance();
					break;
				} else if (RobotUtils.isClickableActive(Clickable.BUTTON_NEXT)) {
					nextState = StateAttack.instance();
					break;
				}

				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
				context.setState(StateNoAction.instance());
			}
		}

		context.setState(nextState);
	}

	public static StateIdle instance() {
		return instance;
	}

}
