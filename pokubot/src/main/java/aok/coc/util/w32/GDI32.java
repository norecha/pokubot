package aok.coc.util.w32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.win32.W32APIOptions;

public interface GDI32 extends com.sun.jna.platform.win32.GDI32 {
	GDI32	INSTANCE	= (GDI32) Native.loadLibrary("gdi32", GDI32.class, W32APIOptions.DEFAULT_OPTIONS);

	boolean BitBlt(HDC hObject, int nXDest, int nYDest, int nWidth, int nHeight, HDC hObjectSource, int nXSrc, int nYSrc, DWORD dwRop);
	
	int GetPixel(HDC hObject, int nXPos, int nYPos);
}

