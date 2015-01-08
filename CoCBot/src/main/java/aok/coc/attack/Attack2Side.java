package aok.coc.attack;

import java.util.Map;

import aok.coc.util.Clickable;
import aok.coc.util.ConfigUtils;
import aok.coc.util.RobotUtils;


public class Attack2Side extends Attack {
	
	private static final Attack2Side	instance	= new Attack2Side();

	public static Attack2Side instance() {
		return instance;
	}

	private Attack2Side() {
	}

	@Override
	public void attack(int[] loot) throws InterruptedException {
		RobotUtils.zoomUp(20);
		
		int totalBarbCount = 0;
		int totalArcherCount = 0;
		for (Map<Clickable, Integer> c : ConfigUtils.instance().getRaxInfo()) {
			if (c.containsKey(Clickable.BUTTON_RAX_BARB)) {
				totalBarbCount += c.get(Clickable.BUTTON_RAX_BARB);
			}
			if (c.containsKey(Clickable.BUTTON_RAX_ARCHER)) {
				totalArcherCount += c.get(Clickable.BUTTON_RAX_ARCHER);
			}
		}
		
		System.out.println("Dropping units.");
		dropUnits(totalBarbCount, totalArcherCount);
		
		checkLootChange(loot);
		System.out.println("No more loot.");
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
