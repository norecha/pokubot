package aok.coc.attack;

import java.util.logging.Logger;

import aok.coc.util.RobotUtils;
import aok.coc.util.coords.Clickable;

public class Attack4Side extends Attack {
	private static final Logger			logger		= Logger.getLogger(Attack4Side.class.getName());

	private static final Attack4Side	instance	= new Attack4Side();

	public static Attack4Side instance() {
		return instance;
	}

	private Attack4Side() {
	}

	@Override
	protected void doDropUnits(int[] attackGroup) throws InterruptedException {
		logger.info("Dropping units from 4 sides.");

		for (int unitIdx = 0; unitIdx < attackGroup.length; unitIdx++) {
			int unitCount = attackGroup[unitIdx];

			// select unit
			RobotUtils.leftClick(Clickable.getButtonAttackUnit(unitIdx + 1), 100);
			
			// we need at least 4 units for 4 sides
			if (unitCount >= 4) {
				int[][] topToRightPoints = pointsBetweenFromToInclusive(TOP_X, TOP_Y, RIGHT_X, RIGHT_Y,
					unitCount / 4 + unitCount % 4);
				int[][] rightToBottomPoints = pointsBetweenFromToInclusive(RIGHT_X, RIGHT_Y, BOTTOM_RIGHT_X, BOTTOM_RIGHT_Y,
					unitCount / 4);
				int[][] bottomToLeftPoints = pointsBetweenFromToInclusive(BOTTOM_LEFT_X, BOTTOM_LEFT_Y, LEFT_X, LEFT_Y,
					unitCount / 4);
				int[][] leftToTopPoints = pointsBetweenFromToInclusive(LEFT_X, LEFT_Y, TOP_X, TOP_Y,
					unitCount / 4);
				
				// drop units
				for (int[][] points : new int[][][]{topToRightPoints, rightToBottomPoints, bottomToLeftPoints, leftToTopPoints}) {
					for (int[] point : points) {
						logger.finest("dropping to: " + point[0] + "," + point[1]);
						RobotUtils.leftClick(point[0], point[1]);
						Thread.sleep(150 + RobotUtils.random.nextInt(100));
					}
				}
			// if less, just drop from top
			} else {
				int[] point = new int[]{TOP_X, TOP_Y};
				for (int i = 0; i < unitCount; i++) {
					logger.finest("dropping to: " + point[0] + "," + point[1]);
					RobotUtils.leftClick(point[0], point[1]);
					Thread.sleep(150 + RobotUtils.random.nextInt(100));
				}
			}
		}
	}
}
