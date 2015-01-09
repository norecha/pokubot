package aok.coc.util;

import java.util.logging.Logger;

import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

import aok.coc.util.coords.Clickable;

public class MouseListener implements NativeMouseListener {
	private static final Logger	logger	= Logger.getLogger(Thread.currentThread().getClass().getName());

	@Override
	public void nativeMouseClicked(NativeMouseEvent e) {
		Clickable.UNIT_FIRST_RAX.setX(e.getX());
		Clickable.UNIT_FIRST_RAX.setY(e.getY());
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
