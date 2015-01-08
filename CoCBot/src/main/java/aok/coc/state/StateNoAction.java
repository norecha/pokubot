package aok.coc.state;

import java.util.logging.Logger;

public class StateNoAction implements State {
	private static final Logger	logger	= Logger.getLogger(StateNoAction.class.getName());

	private final static StateNoAction instance = new StateNoAction();
	
	private StateNoAction() {
	}
	
	@Override
	public void handle(Context context) {
		logger.info("StateNoAction");
	}

	public static StateNoAction instance() {
		return instance;
	}
}
