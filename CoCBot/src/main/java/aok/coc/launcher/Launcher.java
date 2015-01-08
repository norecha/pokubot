package aok.coc.launcher;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import aok.coc.exception.BotConfigurationException;
import aok.coc.state.Context;
import aok.coc.state.StateAttack;
import aok.coc.util.ConfigUtils;
import aok.coc.util.RobotUtils;
import aok.coc.util.SingleLineFormatter;
import aok.coc.util.User32;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;

public class Launcher {

	private static final String windowName = "BlueStacks App Player";
	private static final int BS_RES_X = 860;
	private static final int BS_RES_Y = 720;
	
	private static Logger logger = Logger.getLogger(Launcher.class.getCanonicalName());

	public static void main(String[] args) {
		
		// setup logger
		ConsoleHandler handler = new ConsoleHandler();
		logger.setLevel(Level.ALL);
		handler.setFormatter(new SingleLineFormatter());
		logger.addHandler(handler);
		logger.setUseParentHandlers(false);
		handler.setLevel(Level.ALL);
		
		Launcher launcher = new Launcher();
		
		try {
			launcher.setup();
		} catch (BotConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		launcher.launch();
	}

	private void setup() throws BotConfigurationException {

		HWND hwnd = User32.INSTANCE.FindWindow(null, windowName);
		if (hwnd == null) {
			throw new BotConfigurationException(windowName + " is not found.");
		}

		int[] rect = { 0, 0, 0, 0 };
		int result = User32.INSTANCE.GetWindowRect(hwnd, rect);
		if (result == 0) {
			throw new BotConfigurationException(windowName + " is not found.");
		}
		
		System.out.println(String.format("The corner locations for the window \"%s\" are %s",
			windowName, Arrays.toString(rect)));
		
		Rectangle bsRectangle = new Rectangle(rect[0], rect[1], rect[2] - rect[0], rect[3] - rect[1]);
		
		// setup resolution
		setupResolution(bsRectangle);
		
		RobotUtils.setupWin32(hwnd, bsRectangle);
	}

	private void setupResolution(Rectangle bsRectangle) throws BotConfigurationException {
		if (bsRectangle.width == BS_RES_X && bsRectangle.height == BS_RES_Y) {
			return;
		}
		
		String msg = String.format("%s must run in resolution %dx%d.\n" +
				"Click YES to change it automatically, NO to do it later.\n",
			windowName, BS_RES_X, BS_RES_Y);
		
		boolean ret = RobotUtils.confirmationBox(msg, "Change resolution");
		
		if (!ret) {
			throw new BotConfigurationException("Re-run when resolution is fixed.");
		}
		
		// update registry
		HKEYByReference key = Advapi32Util.registryGetKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\BlueStacks\\Guests\\Android\\FrameBuffer\\0", WinNT.KEY_ALL_ACCESS);
		Advapi32Util.registrySetIntValue(key.getValue(), "WindowWidth", BS_RES_X);
		Advapi32Util.registrySetIntValue(key.getValue(), "WindowHeight", BS_RES_Y);
		Advapi32Util.registrySetIntValue(key.getValue(), "GuestWidth", BS_RES_Y);
		Advapi32Util.registrySetIntValue(key.getValue(), "GuestHeight", BS_RES_Y);
		Advapi32Util.registrySetIntValue(key.getValue(), "FullScreen", 0);
		
		RobotUtils.msgBox("Please restart " + windowName);
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
