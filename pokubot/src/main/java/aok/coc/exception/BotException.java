package aok.coc.exception;

public class BotException extends Exception {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public BotException(String msg) {
		super(msg);
	}

	public BotException(String msg, Throwable t) {
		super(msg, t);
	}
	
}
