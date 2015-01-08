package aok.coc.util;

import java.util.logging.Logger;

import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

public class MouseListener implements NativeMouseListener {
	private static final Logger	logger	= Logger.getLogger(Thread.currentThread().getClass().getName());

	@Override
	public void nativeMouseClicked(NativeMouseEvent e) {
		Clickable.BUTTON_RAX_ICON.setX(e.getX());
		Clickable.BUTTON_RAX_ICON.setY(e.getY());
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent e) {
		// TODO Auto-generated method stub

	}

}
