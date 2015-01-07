package aok.coc.state;

import aok.coc.exception.CocBotException;

public class Context {

	private State current;
	
	public void setState(State state) {
		this.current = state;
	}

	public void handle() {
		try {
			current.handle(this);
		} catch (CocBotException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
