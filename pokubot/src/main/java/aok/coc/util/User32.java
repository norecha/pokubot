package aok.coc.util;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface User32 extends StdCallLibrary {
	User32	INSTANCE	= (User32) Native.loadLibrary("user32", User32.class,
							W32APIOptions.DEFAULT_OPTIONS);

	HWND FindWindow(String lpClassName, String lpWindowName);

	int GetWindowRect(HWND handle, int[] rect);
	
	HWND SetFocus(HWND hWnd);
	boolean SetForegroundWindow(HWND hWnd);
	LRESULT SendMessage(HWND hWnd, int Msg, int wParam, int lParam);
}