package aok.coc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import aok.coc.attack.Attack;
import aok.coc.attack.Attack4Side;
import aok.coc.launcher.Setup;
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

	private boolean				isInitialized				= false;
	private File				configFile;

	private static final String	PROPERTY_GOLD				= "gold";
	private static final String	PROPERTY_ELIXIR				= "elixir";
	private static final String	PROPERTY_DE					= "de";
	private static final String	PROPERTY_MAX_TH				= "max_th";
	private static final String	PROPERTY_IS_MATCH_ALL_CONDS	= "match_all";
	private static final String	PROPERTY_BARRACKS_COORDS	= "barracks_coords";

	private int					goldThreshold				= 0;
	private int					elixirThreshold				= 0;
	private int					darkElixirThreshold			= 0;
	private int					maxThThreshold				= 0;

	private boolean				matchAllConditions			= false;
	private boolean				barracksConfigDone			= false;

	private static final Logger	logger						= Logger.getLogger(ConfigUtils.class.getName());

	public synchronized static void initialize() throws IllegalStateException {
		// Throw exception if called twice
		if (instance.isInitialized) {
			throw new IllegalStateException("ConfigUtils is already initialized.");
		}

		String appdata = System.getenv("appdata");

		File root = new File(appdata, Setup.APP_NAME);
		if (!root.isDirectory()) {
			root.mkdir();
		}

		instance.configFile = new File(root, "config.properties");
		Properties configProperties = new Properties();

		if (instance.configFile.isFile()) {
			try (InputStream is = new FileInputStream(instance.configFile)) {
				configProperties.load(is);

				String goldProperty = configProperties.getProperty(PROPERTY_GOLD);
				if (goldProperty != null) {
					instance.goldThreshold = Integer.parseInt(goldProperty);
				}

				String elixirProperty = configProperties.getProperty(PROPERTY_ELIXIR);
				if (elixirProperty != null) {
					instance.elixirThreshold = Integer.parseInt(elixirProperty);
				}

				String deProperty = configProperties.getProperty(PROPERTY_DE);
				if (deProperty != null) {
					instance.darkElixirThreshold = Integer.parseInt(deProperty);
				}

				String maxThProperty = configProperties.getProperty(PROPERTY_MAX_TH);
				if (maxThProperty != null) {
					instance.maxThThreshold = Integer.parseInt(maxThProperty);
				}

				String matchAllCondsProperty = configProperties.getProperty(PROPERTY_IS_MATCH_ALL_CONDS);
				if (matchAllCondsProperty != null) {
					instance.matchAllConditions = Boolean.parseBoolean(matchAllCondsProperty);
				}

				String barracksCoordsProperty = configProperties.getProperty(PROPERTY_BARRACKS_COORDS);
				if (barracksCoordsProperty != null) {
					try (Scanner sc = new Scanner(barracksCoordsProperty)) {
						int x = sc.nextInt();
						int y = sc.nextInt();

						Clickable.UNIT_FIRST_RAX.setX(x);
						Clickable.UNIT_FIRST_RAX.setY(y);

						logger.info(String.format("Found barracks coordinates <%d, %d>", x, y));
						instance.barracksConfigDone = true;
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Unable to read barracks config.", e);
						instance.barracksConfigDone = false;
					}
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Unable to read configuration file.", e);
			}
		} else {
			try {
				instance.configFile.createNewFile();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Unable to create configuration file.", e);
			}
		}

		instance.isInitialized = true;
	}

	public synchronized static void close() {

		if (!instance.isInitialized) {
			throw new IllegalStateException("ConfigUtils is not initialized.");
		}

		instance.isInitialized = false;
	}

	public void save() {
		try (FileOutputStream fos = new FileOutputStream(configFile)) {
			Properties configProperties = new Properties();
			configProperties.setProperty(PROPERTY_GOLD, String.valueOf(goldThreshold));
			configProperties.setProperty(PROPERTY_ELIXIR, String.valueOf(elixirThreshold));
			configProperties.setProperty(PROPERTY_DE, String.valueOf(darkElixirThreshold));
			configProperties.setProperty(PROPERTY_MAX_TH, String.valueOf(maxThThreshold));
			configProperties.setProperty(PROPERTY_IS_MATCH_ALL_CONDS, String.valueOf(matchAllConditions));
			configProperties.setProperty(PROPERTY_BARRACKS_COORDS, Clickable.UNIT_FIRST_RAX.getX() + " " + Clickable.UNIT_FIRST_RAX.getY());
			configProperties.store(fos, null);
			logger.info("Settings are saved.");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Unable to save configuration file.", e);
		}
	}

	public boolean doConditionsMatch(int gold, int elixir, int de) {
		// if threshold is 0 or not set, do not match based on them
		
		if (isMatchAllConditions()) {
			gold = goldThreshold == 0 ? Integer.MAX_VALUE : gold;
			elixir = elixirThreshold == 0 ? Integer.MAX_VALUE : elixir;
			de = darkElixirThreshold == 0 ? Integer.MAX_VALUE : de;
			return gold >= goldThreshold &&
					elixir >= elixirThreshold &&
					de >= darkElixirThreshold;
		} else {
			gold = goldThreshold == 0 ? Integer.MIN_VALUE : gold;
			elixir = elixirThreshold == 0 ? Integer.MIN_VALUE : elixir;
			de = darkElixirThreshold == 0 ? Integer.MIN_VALUE : de;
			return gold >= goldThreshold ||
					elixir >= elixirThreshold ||
					de >= darkElixirThreshold;
		}
	}

	public Attack getAttackStrategy() {
		return Attack4Side.instance();
	}

	public boolean isAutoAttackEnabled() {
		return false;
	}

	public List<Clickable> getRaxInfo() {
		List<Clickable> list = new ArrayList<>();

		list.add(Clickable.BUTTON_RAX_BARB);
		list.add(Clickable.BUTTON_RAX_BARB);
		list.add(Clickable.BUTTON_RAX_ARCHER);
		list.add(Clickable.BUTTON_RAX_ARCHER);

		return list;
	}

	public int getGoldThreshold() {
		return goldThreshold;
	}

	public void setGoldThreshold(int goldThreshold) {
		this.goldThreshold = goldThreshold;
	}

	public int getElixirThreshold() {
		return elixirThreshold;
	}

	public void setElixirThreshold(int elixirThreshold) {
		this.elixirThreshold = elixirThreshold;
	}

	public int getDarkElixirThreshold() {
		return darkElixirThreshold;
	}

	public void setDarkElixirThreshold(int darkElixirThreshold) {
		this.darkElixirThreshold = darkElixirThreshold;
	}

	public int getMaxThThreshold() {
		return maxThThreshold;
	}

	public void setMaxThThreshold(int maxThThreshold) {
		this.maxThThreshold = maxThThreshold;
	}

	public boolean isMatchAllConditions() {
		return matchAllConditions;
	}

	public void setMatchAllConditions(boolean matchAllConditions) {
		this.matchAllConditions = matchAllConditions;
	}

	public boolean isBarracksConfigDone() {
		return barracksConfigDone;
	}

	public void setBarracksConfigDone(boolean barracksConfigDone) {
		this.barracksConfigDone = barracksConfigDone;
	}

	public static boolean isInitialized() {
		return instance.isInitialized;
	}

}
