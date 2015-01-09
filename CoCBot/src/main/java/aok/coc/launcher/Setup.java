package aok.coc.launcher;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

import aok.coc.exception.BotConfigurationException;
import aok.coc.util.ConfigUtils;
import aok.coc.util.RobotUtils;
import aok.coc.util.User32;
import aok.coc.util.coords.Clickable;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;

public class Setup {

	public static final String	APP_NAME		= "CoCBot";
	private static final String	BS_WINDOW_NAME	= "BlueStacks App Player";
	private static final int	BS_RES_X		= 860;
	private static final int	BS_RES_Y		= 720;

	private static Rectangle	bsRect			= null;
	private static HWND			bsHwnd			= null;

	private static final Logger	logger			= Logger.getLogger(Setup.class.getName());

	public static void setup() throws BotConfigurationException, InterruptedException {
		if (!RobotUtils.SYSTEM_OS.toLowerCase().contains("windows")) {
			throw new BotConfigurationException("Bot is only available for Windows OS.");
		}
		// setup configUtils
		logger.info("Setting up ConfigUtils...");
		ConfigUtils.initialize();

		// setup bs window handle
		logger.info(String.format("Setting up %s window...", BS_WINDOW_NAME));
		setupBsRect();

		// setup resolution
		logger.info(String.format("Setting up %s resolution...", BS_WINDOW_NAME));
		setupResolution();

		// setup RobotUtils
		logger.info("Setting up RobotUtils...");
		RobotUtils.setupWin32(bsHwnd, bsRect);

		// setup barracks
		logger.info("Setting up Barracks...");
		setupBarracks();
	}

	private static void setupBarracks() throws BotConfigurationException, InterruptedException {
		String appdata = System.getenv("appdata");

		File root = new File(appdata, APP_NAME);
		if (!root.isDirectory()) {
			root.mkdir();
		}

		boolean barracksConfigDone = false;
//		boolean wallsConfigDone = false;
		String barracksCoordsProperty = "barracks_coords";
//		String wallsCoordsProperty = "walls_coords";
		File configFile = new File(root, "config.properties");
		Properties configProperties = new Properties();
		if (configFile.isFile()) {
			try (InputStream is = new FileInputStream(configFile)) {
				configProperties.load(is);
				if (configProperties.containsKey(barracksCoordsProperty)) {
					String coords = configProperties.getProperty(barracksCoordsProperty);
					try (Scanner sc = new Scanner(coords)) {
						int x = sc.nextInt();
						int y = sc.nextInt();
	
						Clickable.UNIT_FIRST_RAX.setX(x);
						Clickable.UNIT_FIRST_RAX.setY(y);
						
						logger.info(String.format("Found barracks coordinates <%d, %d>", x, y));
						barracksConfigDone = true;
					}
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Unable to read configuration file. Trying to run barracks setup again...");
			}
		}

		if (!barracksConfigDone) {
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

						x -= bsRect.x;
						y -= bsRect.y;

						Clickable.UNIT_FIRST_RAX.setX(x);
						Clickable.UNIT_FIRST_RAX.setY(y);

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
				logger.info(String.format("Saved barracks location to <%d, %d>",
					Clickable.UNIT_FIRST_RAX.getX(),
					Clickable.UNIT_FIRST_RAX.getY()));

				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException e) {
				throw new BotConfigurationException("Unable to capture mouse movement.", e);
			}
			
			// try to write to properties file
			try {
				configProperties.setProperty(barracksCoordsProperty, Clickable.UNIT_FIRST_RAX.getX() + " " + Clickable.UNIT_FIRST_RAX.getY());
				if (!configFile.isFile()) {
					configFile.createNewFile();
				}
				try (OutputStream os = new FileOutputStream(configFile)) {
					configProperties.store(os, null);
				}
			} catch (IOException e) {
				// recoverable. bot can still run since we have coordinates
				logger.log(Level.SEVERE, "Unable to save configuration file.", e);
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
