package aok.coc.state;

import aok.coc.exception.BotException;

public class Context {

	private State current;
	
	public void setState(State state) {
		this.current = state;
	}

	public void handle() throws BotException, InterruptedException {
		current.handle(this);
	}
	
}
