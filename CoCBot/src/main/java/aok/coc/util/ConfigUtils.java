package aok.coc.util;

import java.util.ArrayList;
import java.util.List;

import aok.coc.attack.Attack;
import aok.coc.attack.Attack4Side;
import aok.coc.util.coords.Clickable;

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
	
	public boolean isUpgradeWalls() {
		return true;
	}
	
	public Attack getAttackStrategy() {
		return Attack4Side.instance();
	}

	public List<Clickable> getRaxInfo() {
		List<Clickable> list = new ArrayList<>();
		
		list.add(Clickable.BUTTON_RAX_BARB);
		list.add(Clickable.BUTTON_RAX_BARB);
		list.add(Clickable.BUTTON_RAX_ARCHER);
		list.add(Clickable.BUTTON_RAX_ARCHER);

		return list;
	}

}
