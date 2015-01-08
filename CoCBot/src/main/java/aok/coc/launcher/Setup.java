package aok.coc.launcher;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.logging.Logger;

import aok.coc.exception.BotConfigurationException;
import aok.coc.util.RobotUtils;
import aok.coc.util.User32;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;

public class Setup {

	private static final String	BS_WINDOW_NAME	= "BlueStacks App Player";
	private static final int	BS_RES_X		= 860;
	private static final int	BS_RES_Y		= 720;

	private static Rectangle	bsRect			= null;
	private static HWND			bsHwnd			= null;

	private static final Logger	logger			= Logger.getLogger(Setup.class.getName());

	public static void setup() throws BotConfigurationException {

		// setup bs window handle
		logger.info(String.format("Setting up %s window.", BS_WINDOW_NAME));
		setupBsRect();

		// setup resolution
		logger.info(String.format("Setting up %s resolution.", BS_WINDOW_NAME));
		setupResolution();

		// setup RobotUtils
		logger.info(String.format("Setting up RobotUtils.", BS_WINDOW_NAME));
		RobotUtils.setupWin32(bsHwnd, bsRect);

	}

	private static void setupBsRect() throws BotConfigurationException {
		bsHwnd = User32.INSTANCE.FindWindow(null, BS_WINDOW_NAME);
		if (bsHwnd == null) {
			throw new BotConfigurationException(BS_WINDOW_NAME + " is not found.");
		}

		int[] rect = { 0, 0, 0, 0 };
		int result = User32.INSTANCE.GetWindowRect(bsHwnd, rect);
		if (result == 0) {
			throw new BotConfigurationException(BS_WINDOW_NAME + " is not found.");
		}

		logger.fine(String.format("The corner locations for the window \"%s\" are %s",
			BS_WINDOW_NAME, Arrays.toString(rect)));

		bsRect = new Rectangle(rect[0], rect[1], rect[2] - rect[0], rect[3] - rect[1]);
	}

	private static void setupResolution() throws BotConfigurationException {
		// update registry
		try {
			HKEYByReference key = Advapi32Util.registryGetKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\BlueStacks\\Guests\\Android\\FrameBuffer\\0", WinNT.KEY_READ | WinNT.KEY_WRITE);

			int w1 = Advapi32Util.registryGetIntValue(key.getValue(), "WindowWidth");
			int h1 = Advapi32Util.registryGetIntValue(key.getValue(), "WindowHeight");
			int w2 = Advapi32Util.registryGetIntValue(key.getValue(), "GuestWidth");
			int h2 = Advapi32Util.registryGetIntValue(key.getValue(), "GuestHeight");

			if (w1 == BS_RES_X && h1 == BS_RES_Y &&
				w2 == BS_RES_X && h2 == BS_RES_Y) {
				return;
			}

			String msg = String.format("%s must run in resolution %dx%d.\n" +
										"Click YES to change it automatically, NO to do it later.\n",
				BS_WINDOW_NAME, BS_RES_X, BS_RES_Y);

			boolean ret = RobotUtils.confirmationBox(msg, "Change resolution");

			if (!ret) {
				throw new BotConfigurationException("Re-run when resolution is fixed.");
			}

			Advapi32Util.registrySetIntValue(key.getValue(), "WindowWidth", BS_RES_X);
			Advapi32Util.registrySetIntValue(key.getValue(), "WindowHeight", BS_RES_Y);
			Advapi32Util.registrySetIntValue(key.getValue(), "GuestWidth", BS_RES_X);
			Advapi32Util.registrySetIntValue(key.getValue(), "GuestHeight", BS_RES_Y);
			Advapi32Util.registrySetIntValue(key.getValue(), "FullScreen", 0);
		} catch (Exception e) {
			throw new BotConfigurationException("Unable to change resolution. Do it manually.", e);
		}

		RobotUtils.msgBox("Please restart " + BS_WINDOW_NAME);
	}

}
