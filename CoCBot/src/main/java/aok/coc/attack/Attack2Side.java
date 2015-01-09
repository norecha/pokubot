package aok.coc.attack;

import java.util.logging.Logger;

import aok.coc.util.ConfigUtils;
import aok.coc.util.RobotUtils;
import aok.coc.util.coords.Clickable;


public class Attack2Side extends Attack {
	
	private static final Logger	logger	= Logger.getLogger(Attack.class.getName());
	
	private static final Attack2Side	instance	= new Attack2Side();

	public static Attack2Side instance() {
		return instance;
	}

	private Attack2Side() {
	}

	@Override
	public void attack(int[] loot) throws InterruptedException {
		RobotUtils.zoomUp();
		
		int totalBarbCount = 0;
		int totalArcherCount = 0;
		for (Clickable c : ConfigUtils.instance().getRaxInfo()) {
			if (c == Clickable.BUTTON_RAX_BARB) {
				totalBarbCount += 60;
			}
			if (c == Clickable.BUTTON_RAX_ARCHER) {
				totalArcherCount += 60;
			}
		}

		logger.info("Dropping units.");
		dropUnits(totalBarbCount, totalArcherCount);
		
		checkLootChange(loot);
		logger.info("No more loot.");
	}


	private void dropUnits(int totalBarbCount, int totalArcherCount) throws InterruptedException {
		
		// TODO change drop values
		int[][] barbPoints = pointsBetweenFromToInclusive(TOP_X, TOP_Y, RIGHT_X, RIGHT_Y, totalBarbCount / 10);
		
		// drop barbs
		RobotUtils.leftClick(Clickable.BUTTON_ATTACK_UNIT_1, 100);
		for (int[] point : barbPoints) {
			RobotUtils.leftClick(point[0], point[1]);
			Thread.sleep(150 + RobotUtils.random.nextInt(100));
		}

		if (totalArcherCount > 0) {
			RobotUtils.leftClick(Clickable.BUTTON_ATTACK_UNIT_2, 100);
			int[][] archerPoints = pointsBetweenFromToInclusive(LEFT_X, LEFT_Y, TOP_X, TOP_Y, totalArcherCount / 10);
			for (int[] point : archerPoints) {
				RobotUtils.leftClick(point[0], point[1]);
				Thread.sleep(150 + RobotUtils.random.nextInt(100));
			}
		}
	}
}
