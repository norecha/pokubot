package aok.coc.exception;


public class BotConfigurationException extends BotException {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public BotConfigurationException(String msg) {
		super(msg);
	}

	public BotConfigurationException(String msg, Throwable t) {
		super(msg, t);
	}
}
