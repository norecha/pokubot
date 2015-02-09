package aok.coc.util.w32;

import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

public interface User32 extends com.sun.jna.platform.win32.User32 {
	User32	INSTANCE	= (User32) Native.loadLibrary("user32", User32.class,
							W32APIOptions.DEFAULT_OPTIONS);

	@Override
	HWND FindWindow(String lpClassName, String lpWindowName);

	int GetWindowRect(HWND handle, int[] rect);

	boolean ClientToScreen(HWND hWnd, POINT lpPoint);

	boolean ScreenToClient(HWND hWnd, POINT lpPoint);

	@Override
	HWND SetFocus(HWND hWnd);

	@Override
	boolean SetForegroundWindow(HWND hWnd);

	LRESULT SendMessage(HWND hWnd, int Msg, int wParam, int lParam);

	HWND GetDlgItem(HWND hDlg, int nIDDlgItem);

	boolean SetWindowPos(HWND hWnd, int hWndInsertAfter, int X, int Y, int cx, int cy, int uFlags);

	HDC GetWindowDC(HWND hWnd);

	boolean BlockInput(boolean block);

	@Override
	short GetAsyncKeyState(int key);

	short GetKeyState(int key);
}