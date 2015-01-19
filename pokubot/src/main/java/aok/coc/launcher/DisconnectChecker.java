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

	public DisconnectChecker(Context context, Thread mainThread) {
		this.context = context;
		this.mainThread = mainThread;
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

					// temporarily pause StateIdle because in case current state is StateIdle
					// when you click reload, screen would look like it is loaded for a second, before
					// loading actually starts and next state would be executed.
					context.setState(StateIdle.instance());
					context.setTempInterrupted(true);
					mainThread.interrupt();
					StateIdle.instance().setReloading(true);
					Thread.sleep(1500);
					RobotUtils.leftClick(Clickable.UNIT_RECONNECT, 5000);
					StateIdle.instance().setReloading(false);
				}
				Thread.sleep(10000);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
