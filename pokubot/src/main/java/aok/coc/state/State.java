package aok.coc.state;

import aok.coc.exception.BotException;

public interface State {

	public void handle(Context context) throws BotException, InterruptedException;
}
