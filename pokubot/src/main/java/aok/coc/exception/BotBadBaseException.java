package aok.coc.exception;

public class BotBadBaseException extends BotException {

	private static final long	serialVersionUID	= 1L;

	public BotBadBaseException(String msg) {
		super(msg);
	}

	public BotBadBaseException(String msg, Throwable t) {
		super(msg, t);
	}
}
