package aok.coc.state;

import aok.coc.exception.BotException;

public class Context {

	private State current;
	
	public void setState(State state) {
		this.current = state;
	}

	public void handle() {
		try {
			current.handle(this);
		} catch (BotException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
