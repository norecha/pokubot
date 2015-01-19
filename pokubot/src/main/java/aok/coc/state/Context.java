package aok.coc.state;

import aok.coc.exception.BotException;

public class Context {

	private State current;
	private boolean tempInterrupted = false;
	
	public void setState(State state) {
		this.current = state;
	}

	public void handle() throws BotException, InterruptedException {
		current.handle(this);
	}

	public void setTempInterrupted(boolean tempInterrupted) {
		this.tempInterrupted = tempInterrupted;
	}

	public boolean isTempInterrupted() {
		return tempInterrupted;
	}
	
}
