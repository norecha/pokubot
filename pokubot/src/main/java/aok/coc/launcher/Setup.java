package aok.coc.launcher;

import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

import aok.coc.exception.BotConfigurationException;
import aok.coc.util.ConfigUtils;
import aok.coc.util.RobotUtils;
import aok.coc.util.coords.Clickable;
import aok.coc.util.w32.User32;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;

public class Setup {

	public static final String	APP_NAME		= "PokuBot";
	private static final String	BS_WINDOW_NAME	= "BlueStacks App Player";
	public static final int		BS_RES_X		= 860;
	public static final int		BS_RES_Y		= 720;

	private static HWND			bsHwnd			= null;

	private static final Logger	logger			= Logger.getLogger(Setup.class.getName());

	public static void setup() throws BotConfigurationException, InterruptedException {
		// set system locale to ROOT, Turkish clients will break because jnativehook dependency has Turkish I bug
		Locale.setDefault(Locale.ROOT);
		
		if (!RobotUtils.SYSTEM_OS.toLowerCase(Locale.ROOT).contains("windows")) {
			throw new BotConfigurationException("Bot is only available for Windows OS.");
		}
		
		// disable display off
//		Kernel32.INSTANCE.SetThreadExecutionState(Kernel32.ES_SYSTEM_REQUIRED | Kernel32.ES_CONTINUOUS | Kernel32.ES_DISPLAY_REQUIRED);

		// setup configUtils
		logger.info("Setting up ConfigUtils...");
		logger.info("Make sure in-game language is English.");
		ConfigUtils.initialize();

		// setup bs window handle
		logger.info(String.format("Setting up %s window...", BS_WINDOW_NAME));
		setupBsRect();

		// setup resolution
		logger.info(String.format("Setting up %s resolution...", BS_WINDOW_NAME));
		setupResolution();

		// setup RobotUtils
		logger.info("Setting up RobotUtils...");
		RobotUtils.setupWin32(bsHwnd);

		// setup barracks
		logger.info("Setting up Barracks...");
		setupBarracks();
	}

	public static void tearDown() {
		if (ConfigUtils.isInitialized()) {
			ConfigUtils.close();
		}
	}

	private static void setupBarracks() throws BotConfigurationException, InterruptedException {

		if (!ConfigUtils.instance().isBarracksConfigDone()) {
			RobotUtils.zoomUp();
			boolean confirmed = RobotUtils.confirmationBox("You must configure the location " +
															"of first Barracks. First Barracks is the leftmost one when you \n" +
															"scroll through your barracks via orange next arrow on the right. For example, if you \n" +
															"have 4 barracks, when you select the first one and click 'Train Troops', all \n" +
															"3 'next' views should also be barracks.\n\n" +
															"Click Yes to start configuration and click on your first barracks. Do \n" +
															"NOT click anything else in between. Click Yes and click barracks. \n\n" +
															"Make sure you are max zoomed out.",
				"Barracks configuration");

			if (!confirmed) {
				throw new BotConfigurationException("Cannot proceed without barracks");
			}

			// read mouse click
			try {
				GlobalScreen.registerNativeHook();
				GlobalScreen.getInstance().addNativeMouseListener(new NativeMouseListener() {

					@Override
					public void nativeMouseReleased(NativeMouseEvent e) {
					}

					@Override
					public void nativeMousePressed(NativeMouseEvent e) {
					}

					@Override
					public void nativeMouseClicked(NativeMouseEvent e) {
						// not relative to window
						int x = e.getX();
						int y = e.getY();
						logger.finest(String.format("clicked %d %d", e.getX(), e.getY()));
						
						POINT screenPoint = new POINT(x, y);
						User32.INSTANCE.ScreenToClient(bsHwnd, screenPoint);
						
						Clickable.UNIT_FIRST_RAX.setX(screenPoint.x);
						Clickable.UNIT_FIRST_RAX.setY(screenPoint.y);
						
						synchronized (GlobalScreen.getInstance()) {
							GlobalScreen.getInstance().notify();
						}
					}
				});

				logger.info("Waiting for user to click on first barracks.");
				synchronized (GlobalScreen.getInstance()) {
					while (Clickable.UNIT_FIRST_RAX.getX() == null) {
						GlobalScreen.getInstance().wait();
					}
				}
				logger.info(String.format("Set barracks location to <%d, %d>",
					Clickable.UNIT_FIRST_RAX.getX(),
					Clickable.UNIT_FIRST_RAX.getY()));

				ConfigUtils.instance().setBarracksConfigDone(true);

				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException e) {
				throw new BotConfigurationException("Unable to capture mouse movement.", e);
			}
		}
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

		logger.finest(String.format("The corner locations for the window \"%s\" are %s",
			BS_WINDOW_NAME, Arrays.toString(rect)));
		
		// set bs always on top
		int SWP_NOSIZE = 0x0001;
		int SWP_NOMOVE = 0x0002;
		int TOPMOST_FLAGS = SWP_NOMOVE | SWP_NOSIZE;
		
		User32.INSTANCE.SetWindowPos(bsHwnd, -1, 0, 0, 0, 0, TOPMOST_FLAGS);
	}

	private static void setupResolution() throws BotConfigurationException {
		// update registry
		try {
			HKEYByReference key = Advapi32Util.registryGetKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\BlueStacks\\Guests\\Android\\FrameBuffer\\0", WinNT.KEY_READ | WinNT.KEY_WRITE);

			int w1 = Advapi32Util.registryGetIntValue(key.getValue(), "WindowWidth");
			int h1 = Advapi32Util.registryGetIntValue(key.getValue(), "WindowHeight");
			int w2 = Advapi32Util.registryGetIntValue(key.getValue(), "GuestWidth");
			int h2 = Advapi32Util.registryGetIntValue(key.getValue(), "GuestHeight");

			HWND control = User32.INSTANCE.GetDlgItem(bsHwnd, 0);
			int[] rect = new int[4];
			User32.INSTANCE.GetWindowRect(control, rect);
			
			int bsX = rect[2] - rect[0];
			int bsY = rect[3] - rect[1];
			
			if (bsX == BS_RES_X && bsY == BS_RES_Y) {
				return;
			}
			
			logger.warning(String.format("%s resolution is <%d, %d>", BS_WINDOW_NAME, bsX, bsY));
			
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

			throw new BotConfigurationException("Please restart " + BS_WINDOW_NAME);
		} catch (BotConfigurationException e) {
			throw e;
		} catch (Exception e) {
			throw new BotConfigurationException("Unable to change resolution. Do it manually.", e);
		}
	}

}
