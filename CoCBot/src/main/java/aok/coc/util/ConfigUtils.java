package aok.coc.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.WinDef.HWND;

public class ConfigUtils {

	//----------------------------------------------------------
	// Singleton reference
	// Thread safe
	private static final ConfigUtils	instance	= new ConfigUtils();

	/**
	 * Singleton accessor method.
	 */
	public static ConfigUtils instance() {
		if (!instance.isInitialized) {
			synchronized (ConfigUtils.class) {
				if (!instance.isInitialized) {
					throw new IllegalStateException("ConfigUtils is not initialized.");
				}
			}
		}
		return instance;
	}

	// Private constructor - must be singleton
	private ConfigUtils() {
		// NOTE: Must remain empty.
	}

	//----------------------------------------------------------

	private boolean	isInitialized	= false;

	public synchronized static void initialize() throws IllegalStateException {
		// Throw exception if called twice
		if (instance.isInitialized) {
			throw new IllegalStateException("ConfigUtils is already initialized.");
		}

		instance.isInitialized = true;
	}

	public synchronized static void close() {

		if (!instance.isInitialized) {
			throw new IllegalStateException("ConfigUtils is not initialized.");
		}

		instance.isInitialized = false;
	}

	public int getGoldThreshold() {
		return 100_000;
	}

	public int getElixirThreshold() {
		return 100_000;
	}

	public int getDarkElixirThreshold() {
		return 0;
	}

	public boolean isMatchAllConditions() {
		return false;
	}

	/**
	 * 
	 * @return List of Map where each map is from Troop type to number of times they will be trained
	 */
	public List<Map<Clickable, Integer>> getRaxInfo() {
		List<Map<Clickable, Integer>> list = new ArrayList<>();

		// rax 1
		Map<Clickable, Integer> rax1 = new LinkedHashMap<>();
		rax1.put(Clickable.BUTTON_RAX_BARB, 60);

		// rax 2
		Map<Clickable, Integer> rax2 = new LinkedHashMap<>();
		rax2.put(Clickable.BUTTON_RAX_ARCHER, 60);

		// rax 3
		Map<Clickable, Integer> rax3 = new LinkedHashMap<>();
		rax3.put(Clickable.BUTTON_RAX_BARB, 30);
		rax3.put(Clickable.BUTTON_RAX_ARCHER, 30);

		list.add(rax1);
		list.add(rax2);
		list.add(rax3);

		return list;
	}

	public static int[] getRect(String windowName) throws WindowNotFoundException,
			GetWindowRectException {
		HWND hwnd = User32.INSTANCE.FindWindow(null, windowName);
		if (hwnd == null) {
			throw new WindowNotFoundException("", windowName);
		}

		//User32.INSTANCE.SetForegroundWindow(hwnd);
		System.out.printf("Setting focus to %s window, do not click elsewhere!\n", windowName);

		int[] rect = { 0, 0, 0, 0 };
		int result = User32.INSTANCE.GetWindowRect(hwnd, rect);
		if (result == 0) {
			throw new GetWindowRectException(windowName);
		}

		//int lParam = (short)613 | (364<< 16);
		//while ("asd" instanceof String) {
		//User32.INSTANCE.SendMessage(hwnd, 0x201, 0x00000001, lParam);
		//User32.INSTANCE.SendMessage(hwnd, 0x202, 0x00000000, lParam);
		//try {
		//	Thread.sleep(1000);
		//} catch (InterruptedException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		//}
		//System.exit(1);
		return rect;
	}

	@SuppressWarnings("serial")
	public static class WindowNotFoundException extends Exception {
		public WindowNotFoundException(String className, String windowName) {
			super(String.format("Window null for className: %s; windowName: %s",
				className, windowName));
		}
	}

	@SuppressWarnings("serial")
	public static class GetWindowRectException extends Exception {
		public GetWindowRectException(String windowName) {
			super("Window Rect not found for " + windowName);
		}
	}
}
