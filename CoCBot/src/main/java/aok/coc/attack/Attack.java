package aok.coc.attack;

import java.util.logging.Logger;

import aok.coc.util.ImageParser;
import aok.coc.util.RobotUtils;

public abstract class Attack {
	private static final Logger	logger			= Logger.getLogger(Attack.class.getName());

	protected static int		TOP_X			= 429;
	protected static int		TOP_Y			= 32;

	protected static int		LEFT_X			= 66;
	protected static int		LEFT_Y			= 307;

	protected static int		RIGHT_X			= 801;
	protected static int		RIGHT_Y			= 307;

	protected static int		BOTTOM_LEFT_X	= 349;
	protected static int		BOTTOM_LEFT_Y	= 538;

	protected static int		BOTTOM_RIGHT_X	= 506;
	protected static int		BOTTOM_RIGHT_Y	= 538;

	protected abstract void doDropUnits(int[] attackGroup) throws InterruptedException;

	public void attack(int[] loot, int[] attackGroup) throws InterruptedException {
		logger.info("Attacking...");
		RobotUtils.zoomUp();

		doDropUnits(attackGroup);

		checkLootChange(loot);
		logger.info("No more loot.");
	}

	protected static final int[][] pointsBetweenFromToInclusive(int fromX, int fromY, int toX, int toY, int count) {

		if (count < 1) {
			throw new IllegalArgumentException(count + "");
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

	protected void checkLootChange(int[] loot) throws InterruptedException {
		Thread.sleep(10000);
		int[] prevLoot = loot;
		int diff = Integer.MAX_VALUE;
		int delta = 500;
		while (diff > delta) {
			Thread.sleep(10000);

			int[] currLoot = ImageParser.parseLoot();

			diff = 0;
			for (int i = 0; i < prevLoot.length; i++) {
				diff += prevLoot[i] - currLoot[i];
			}
			prevLoot = loot;
		}
	}
}
