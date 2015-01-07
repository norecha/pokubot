package aok.coc;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import aok.coc.state.Context;
import aok.coc.state.StateAttack;
import aok.coc.util.ConfigUtils;
import aok.coc.util.ConfigUtils.GetWindowRectException;
import aok.coc.util.ConfigUtils.WindowNotFoundException;
import aok.coc.util.RobotUtils;
import aok.coc.util.SingleLineFormatter;
import aok.coc.util.User32;

import com.sun.jna.platform.win32.WinDef.HWND;

public class Launcher {
	
	private static Logger logger = Logger.getLogger(Launcher.class.getCanonicalName());

	public static void main(String[] args) throws Exception {
		
		// setup logger
		ConsoleHandler handler = new ConsoleHandler();
		logger.setLevel(Level.ALL);
		handler.setFormatter(new SingleLineFormatter());
		logger.addHandler(handler);
		logger.setUseParentHandlers(false);
		handler.setLevel(Level.ALL);
		
		Launcher launcher = new Launcher();
		launcher.setup();
		launcher.launch();
	}

	private void setup() throws WindowNotFoundException, GetWindowRectException {
		String windowName = "BlueStacks App Player";

		HWND hwnd = User32.INSTANCE.FindWindow(null, windowName);
		if (hwnd == null) {
			throw new WindowNotFoundException("", windowName);
		}

//		User32.INSTANCE.SetForegroundWindow(hwnd);
//		System.out.printf("Setting focus to %s window, do not click elsewhere!\n", windowName);

		int[] rect = { 0, 0, 0, 0 };
		int result = User32.INSTANCE.GetWindowRect(hwnd, rect);
		if (result == 0) {
			throw new GetWindowRectException(windowName);
		}
		
		System.out.println(String.format("The corner locations for the window \"%s\" are %s",
			windowName, Arrays.toString(rect)));
		
		Rectangle bsRectangle = new Rectangle(rect[0], rect[1], rect[2] - rect[0], rect[3] - rect[1]);
		
		RobotUtils.setupWin32(hwnd, bsRectangle);
	}

	private void launch() {
		ConfigUtils.initialize();
		Context context = new Context();
		context.setState(StateAttack.instance());
		while (true) {
			if (Thread.interrupted()) {
				break;
			}
			context.handle();
		}
	}
}
