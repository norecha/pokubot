package aok.coc.state;

import aok.coc.exception.CocBotException;

public interface State {

	public void handle(Context context) throws CocBotException;
}
