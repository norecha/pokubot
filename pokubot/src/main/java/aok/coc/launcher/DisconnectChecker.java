package aok.coc.launcher;

import java.util.logging.Level;
import java.util.logging.Logger;

import aok.coc.state.Context;
import aok.coc.state.StateIdle;
import aok.coc.util.RobotUtils;
import aok.coc.util.coords.Clickable;

public class DisconnectChecker implements Runnable {

	private static final Logger	logger	= Logger.getLogger(DisconnectChecker.class.getName());
	private final Context		context;
	private final Thread				mainThread;
	private final BotLauncher	botLauncher;

	public DisconnectChecker(Context context, Thread mainThread, BotLauncher botLauncher) {
		this.context = context;
		this.mainThread = mainThread;
		this.botLauncher = botLauncher;
	}

	@Override
	public void run() {
		logger.info("Running disconnect detector...");
		try {
			while (true) {
				if (Thread.interrupted()) {
					throw new InterruptedException("Disconnect detector is interrupted.");
				}

				if (RobotUtils.isClickableActive(Clickable.UNIT_BLUESTACKS_DC)) {
					logger.info("Detected disconnect.");
					
					// There are 2 cases:
					// 1. launcher was running and it will be interrupted.
					// It will go back to StateIdle start running immediately.
					
					// 2. launcher was already stopped and was waiting to be woken up by this.
					
					synchronized (context) {
						// case 1
						if (!botLauncher.isWaitingForDcChecker()) {
							context.setDisconnected(true);
							
							// to fix a potential race condition.
							// if bot launcher throws an exception and this
							// gets the context lock right before bot launcher,
							// we don't want bot launcher to wait for this
							context.setWaitDone(true);
							
							mainThread.interrupt();
						// case 2
						} else {
							context.setWaitDone(true);
							context.notify();
						}
					}
					// temporarily pause StateIdle because in case current state is StateIdle
					// when you click reload, screen would look like it is loaded for a second, before
					// loading actually starts and next state would be executed.
					StateIdle.instance().setReloading(true);
					RobotUtils.leftClick(Clickable.UNIT_RECONNECT, 5000);
					Thread.sleep(2000);
					StateIdle.instance().setReloading(false);
				}
				Thread.sleep(10000);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "dc checker: " + e.getMessage(), e);
		}
	}
}
