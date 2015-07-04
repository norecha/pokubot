package aok.coc.launcher;

import aok.coc.exception.BotConfigurationException;
import aok.coc.exception.BotException;
import aok.coc.util.ConfigUtils;
import aok.coc.util.RobotUtils;
import aok.coc.util.w32.User32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.sun.jna.platform.win32.WinUser;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Logger;

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

		verifyMacAddress();

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
	}

	public static void tearDown() {
		if (ConfigUtils.isInitialized()) {
			ConfigUtils.close();
		}
	}

	public static int[] moveBSWindow(int x, int y) {
		logger.finest(String.format("Moving %s to %d, %d", BS_WINDOW_NAME, x, y));
		int[] rect = { 0, 0, 0, 0 };
		User32.INSTANCE.GetWindowRect(bsHwnd, rect);

		int width = rect[2] - rect[0];
		int height = rect[3] - rect[1];
		User32.INSTANCE.MoveWindow(bsHwnd, x, y, width, height, true);

		return rect;
	}

	public static boolean isBSMinimized() {
		return User32.INSTANCE.IsIconic(bsHwnd);
	}

	public static void failIfBSMinimized() throws BotException {
		if (isBSMinimized()) {
			throw new BotException(String.format("Do NOT minimize %s window!", BS_WINDOW_NAME));
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

	private static void verifyMacAddress() throws BotConfigurationException {

		final String base64encryptedMacBytes = "PHM9lAhgGluTsbdLV0bKl1EbooMeR6H6n+TXd7mzEAKYcjV/2CfBjY66CLzuHp2/0w4Fv8d3suE8\n" +
				"YwGOTohdtG1SIScQJWfd0kEOkzvFSXgk364DG3XnYds3v2O+gqhLbpaLpp+Gc1P4J8X+P3akS4LI\n" +
				"I5jF3eUHBeMP0OdJeoc=";

		final String pubKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLUffFDTekzgQKT2eVXqTUVBOs1LqN+pj4dCSt\n" +
				"tPD5VZtk5nH/vs+KGV489Ocl83KCwLK0iVMiNTqjDf46vTn8mWlslUqZOiowN3VMovgfjgU9EqfN\n" +
				"eHR5uq/QgS5lAACiDu67y6teqIqLaiqlLKHnAvtdTRzrKIvzLHg6PKZx3QIDAQAB\n";

		try {
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] encryptedMacBytes = decoder.decodeBuffer(base64encryptedMacBytes);

			PublicKey pubKey = decodePublicKey(pubKeyStr);
			byte[] expectedMacBytes = decrypt(encryptedMacBytes, pubKey);
			String expectedMac = new String(expectedMacBytes).trim();

			InetAddress ip = InetAddress.getLocalHost();

			NetworkInterface network = NetworkInterface.getByInetAddress(ip);

			byte[] actualMacBytes = network.getHardwareAddress();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < actualMacBytes.length; i++) {
				sb.append(String.format("%02X%s", actualMacBytes[i], (i < actualMacBytes.length - 1) ? "-" : ""));
			}
			String actualMac = sb.toString();

			if (!actualMac.equals(expectedMac)) {
				throw new BotConfigurationException("Authentication failed.");
			}
		} catch (BotConfigurationException e) {
			throw e;
		} catch (Exception e) {
			throw new BotConfigurationException("Unable to get mac address", e);
		}
	}

	private static PublicKey decodePublicKey(String keyStr) throws Exception {

		BASE64Decoder decoder = new BASE64Decoder();
		byte[] sigBytes2 = decoder.decodeBuffer(keyStr);

		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes2);

		KeyFactory keyFact = KeyFactory.getInstance("RSA");
		return keyFact.generatePublic(x509KeySpec);
	}

	private static byte[] decrypt(byte[] inpBytes, PublicKey key) throws Exception {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("RSA/ECB/NoPadding");
		} catch (NoSuchAlgorithmException e) {
			cipher = Cipher.getInstance("RSA/NONE/NoPadding");
		}
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(inpBytes);
	}
}
