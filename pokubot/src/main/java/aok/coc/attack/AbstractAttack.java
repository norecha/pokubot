package aok.coc.attack;

import java.util.logging.Logger;

import aok.coc.exception.BotBadBaseException;
import aok.coc.util.ImageParser;
import aok.coc.util.RobotUtils;

public abstract class AbstractAttack {
	private static final Logger	logger			= Logger.getLogger(AbstractAttack.class.getName());

	protected static int		TOP_X			= 429;
	protected static int		TOP_Y			= 18;

	protected static int		LEFT_X			= 19;
	protected static int		LEFT_Y			= 307;

	protected static int		RIGHT_X			= 836;
	protected static int		RIGHT_Y			= 307;

	protected static int		BOTTOM_LEFT_X	= 300;
	protected static int		BOTTOM_LEFT_Y	= 536;

	protected static int		BOTTOM_RIGHT_X	= 537;
	protected static int		BOTTOM_RIGHT_Y	= 538;
	
	protected static final int	PAUSE_BETWEEN_UNIT_DROP = 61;

	protected abstract void doDropUnits(int[] attackGroup) throws InterruptedException;

	public void attack(int[] loot, int[] attackGroup) throws InterruptedException {
		logger.info("Attacking...");
		RobotUtils.zoomUp();

		doDropUnits(attackGroup);

		sleepUntilLootDoesNotChange(loot);
		logger.info("No more loot.");
	}

	protected static final int[][] pointsBetweenFromToInclusive(int fromX, int fromY, int toX, int toY, int count) {

		if (count <= 0) {
			return new int[0][0];
		} else if (count == 1) {
			return new int[][] { { (toX + fromX) / 2, (toY + fromY) / 2 } };
		}

		int[][] result = new int[count][2];

		double deltaX = (toX - fromX) / (count - 1);
		double deltaY = (toY - fromY) / (count - 1);

		for (int i = 0; i < count; i++) {
			result[i][0] = (int) (fromX + deltaX * i);
			result[i][1] = (int) (fromY + deltaY * i);
		}

		return result;
	}

	protected void sleepUntilLootDoesNotChange(int[] loot) throws InterruptedException {
		Thread.sleep(10000);
		int[] prevLoot = loot;
		int diff = Integer.MAX_VALUE;
		int delta = 500;
		while (diff > delta) {
			Thread.sleep(15000);

			int[] currLoot;
			try {
				currLoot = ImageParser.parseLoot();
			} catch (BotBadBaseException e) {
				Thread.sleep(2000);
				// in case of 100% win/no troops left, attack screen will end prematurely.
				return;
			}

			diff = 0;
			for (int i = 0; i < prevLoot.length; i++) {
				// in case of wrong parsing
				// [01.21.15 11:45:13 PM] INFO: [gold: 20174, elixir: 93476, de: 218] 
				// [01.21.15 11:45:28 PM] INFO: [gold: 201758, elixir: 31364, de: 202] 
				
				diff += prevLoot[i] > currLoot[i] ? prevLoot[i] - currLoot[i] : 0;
			}
			prevLoot = currLoot;
		}
	}
}
