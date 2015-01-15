package aok.coc.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import aok.coc.exception.BotConfigurationException;
import aok.coc.exception.BotException;
import aok.coc.state.Context;
import aok.coc.state.StateAttack;

public class Launcher {

	private static final Logger	logger	= Logger.getLogger(Launcher.class.getName());

	static {
		try (InputStream inputStream = Launcher.class.getResourceAsStream("/logging.properties")) {
			LogManager.getLogManager().readConfiguration(inputStream);
		} catch (final IOException e) {
			Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
			Logger.getAnonymousLogger().severe(e.getMessage());
		}
	}

	public static void main(String[] args) {
		// run the bot
		Launcher launcher = new Launcher();
		try {
			launcher.start();
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			System.exit(1);
		} catch (BotConfigurationException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			System.exit(2);
		} catch (BotException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			System.exit(3);
		}
	}

	public void start() throws BotConfigurationException, BotException, InterruptedException {
		// setup the bot
		Setup.setup();
		
		// state pattern
		Context context = new Context();
		context.setState(StateAttack.instance());
		
		// start daemon thread that checks if you are DC'ed etc
		logger.info("Starting disconnect detector...");
		Thread dcThread = new Thread(new DisconnectChecker(context), "DisconnectCheckerThread");
		dcThread.setDaemon(true);
		dcThread.start();
		
		while (true) {
			if (Thread.interrupted()) {
				dcThread.interrupt();
				throw new InterruptedException("Launcher is interrupted.");
			}
			context.handle();
		}
	}
}
