package aok.coc.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import aok.coc.exception.BotConfigurationException;
import aok.coc.exception.BotException;
import aok.coc.state.Context;
import aok.coc.state.StateIdle;

public class BotLauncher {

	private static final Logger	logger	= Logger.getLogger(BotLauncher.class.getName());

	public static void main(String[] args) {
		try (InputStream inputStream = BotLauncher.class.getResourceAsStream("/logging.properties")) {
			LogManager.getLogManager().readConfiguration(inputStream);
		} catch (final IOException e) {
			Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
			Logger.getAnonymousLogger().severe(e.getMessage());
		}

		// run the bot
		BotLauncher launcher = new BotLauncher();
		try {
			launcher.setup();
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
		} finally {
			for (Handler h : Logger.getLogger("").getHandlers()) {
				h.close();
			}
		}
	}

	public void setup() throws BotConfigurationException, InterruptedException {
		// setup the bot
		Setup.setup();
	}

	public void tearDown() {
		Setup.tearDown();
	}

	public void start() throws BotException, InterruptedException {
		// state pattern
		Context context = new Context();
		context.setState(StateIdle.instance());

		// start daemon thread that checks if you are DC'ed etc
		logger.info("Starting disconnect detector...");
		Thread dcThread = new Thread(new DisconnectChecker(context), "DisconnectCheckerThread");
		dcThread.setDaemon(true);
		dcThread.start();

		try {
			while (true) {
				if (Thread.interrupted()) {
					dcThread.interrupt();
					throw new InterruptedException("Launcher is interrupted.");
				}
				context.handle();
			}
		} finally {
			dcThread.interrupt();
		}
	}
}
