package aok.coc.util;

import aok.coc.util.coords.Area;
import aok.coc.util.w32.User32;
import com.sun.jna.platform.win32.WinDef;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Rifqi on 8/9/2015.
 */
public class TestRobotUtils {

    private static WinDef.HWND bsHwnd;
    private static final String	BS_WINDOW_NAME	= "BlueStacks App Player";

    @Before
    public void setupWin32() {
        bsHwnd = User32.INSTANCE.FindWindow(null, BS_WINDOW_NAME);
    }

    @Before
    public void setupRobotUtil() {
        RobotUtils.setupWin32(bsHwnd);
    }

    @Test
    public void testSaveScreenShot() throws IOException {
        RobotUtils.saveScreenShot(Area.SAMPLE_SCREEN, "debug", "base_" + System.currentTimeMillis());
    }

    @Test
    public void testZoomUp() throws InterruptedException {
        RobotUtils.zoomUp();
    }
}
