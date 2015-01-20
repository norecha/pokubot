package aok.coc.state;

import java.util.logging.Logger;

import aok.coc.exception.BotException;

public class Context {
	private static final Logger	logger	= Logger.getLogger(Context.class.getName());

	private State current;
	private boolean disconnected = false;
	private boolean waitDone = false;
	
	public void setState(State state) {
		logger.finest("Setting next state to: " + state.getClass().getSimpleName());
		this.current = state;
	}

	public void handle() throws BotException, InterruptedException {
		current.handle(this);
	}

	public boolean isDisconnected() {
		return disconnected;
	}

	public void setDisconnected(boolean disconnected) {
		this.disconnected = disconnected;
	}

	public boolean isWaitDone() {
		return waitDone;
	}

	public void setWaitDone(boolean waitDone) {
		this.waitDone = waitDone;
	}
}
