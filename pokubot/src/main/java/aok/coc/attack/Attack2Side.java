package aok.coc.attack;

import java.util.logging.Logger;

import aok.coc.util.RobotUtils;
import aok.coc.util.coords.Clickable;


public class Attack2Side extends Attack {
	
	private static final Logger	logger	= Logger.getLogger(Attack2Side.class.getName());
	
	private static final Attack2Side	instance	= new Attack2Side();

	public static Attack2Side instance() {
		return instance;
	}

	private Attack2Side() {
	}

	@Override
	protected void doDropUnits(int[] attackGroup) throws InterruptedException {
		logger.info("Dropping units from 2 sides.");
		
		for (int unitIdx = 0; unitIdx < attackGroup.length; unitIdx++) {
			int unitCount = attackGroup[unitIdx];
			
			// select unit
			RobotUtils.leftClick(Clickable.getButtonAttackUnit(unitIdx + 1), 100);
			
			if (unitCount == 0) {
				continue;
			} else if (unitCount == 1) { // BK etc
				// drop from top
				logger.finest("dropping to: " + TOP_X + "," + TOP_Y);
				RobotUtils.leftClick(TOP_X, TOP_Y);
				Thread.sleep(150 + RobotUtils.random.nextInt(100));
			} else {
				int[][] topToRightPoints = pointsBetweenFromToInclusive(TOP_X, TOP_Y, RIGHT_X, RIGHT_Y,
					unitCount / 2 + unitCount % 2);
				int[][] topToLeftPoints = pointsBetweenFromToInclusive(TOP_X, TOP_Y, LEFT_X, LEFT_Y,
					unitCount / 2);
				
				// drop units
				for (int[][] points : new int[][][]{topToRightPoints, topToLeftPoints}) {
					for (int[] point : points) {
						RobotUtils.leftClick(point[0], point[1]);
						Thread.sleep(150 + RobotUtils.random.nextInt(100));
					}
				}
			}
		}
	}
}
