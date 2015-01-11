package aok.coc.state;

import java.util.logging.Logger;

import aok.coc.exception.BotException;

public class StateNoAction implements State {

	private static final Logger		logger		= Logger.getLogger(StateNoAction.class.getName());

	private static StateNoAction	instance	= new StateNoAction();

	private StateNoAction() {
	}

	@Override
	public void handle(Context context) throws BotException, InterruptedException {
		logger.info("StateNoAction");
	}

	public static StateNoAction instance() {
		return instance;
	}

}
