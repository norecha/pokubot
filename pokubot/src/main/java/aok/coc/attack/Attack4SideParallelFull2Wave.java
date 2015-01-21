package aok.coc.attack;

import java.util.logging.Logger;

import aok.coc.util.RobotUtils;
import aok.coc.util.coords.Clickable;

public class Attack4SideParallelFull2Wave extends AbstractAttack {

	private static final Logger						logger		= Logger.getLogger(Attack4SideParallelFull2Wave.class.getName());

	private static final Attack4SideParallelFull2Wave	instance	= new Attack4SideParallelFull2Wave();

	public static Attack4SideParallelFull2Wave instance() {
		return instance;
	}

	private Attack4SideParallelFull2Wave() {
	}

	@Override
	protected void doDropUnits(int[] attackGroup) throws InterruptedException {
		logger.info("Dropping units from 4 sides in parallel in 2 full waves.");

		for (int wave = 0; wave < 2; wave++) {
			for (int unitIdx = 0; unitIdx < attackGroup.length; unitIdx++) {
				int unitCount = attackGroup[unitIdx];
				unitCount = unitCount / 2 + wave * (unitCount % 2);

				// select unit
				RobotUtils.leftClick(Clickable.getButtonAttackUnit(unitIdx + 1), 100);

				int[][] topToRightPoints = pointsBetweenFromToInclusive(TOP_X, TOP_Y, RIGHT_X, RIGHT_Y,
					unitCount / 4 + unitCount % 4);
				int[][] topToLeftPoints = pointsBetweenFromToInclusive(TOP_X, TOP_Y, LEFT_X, LEFT_Y,
					unitCount / 4);
				
				int[][] rightToBottomPoints = pointsBetweenFromToInclusive(RIGHT_X, RIGHT_Y, BOTTOM_RIGHT_X, BOTTOM_RIGHT_Y,
					unitCount / 4);
				int[][] leftToBottomPoints = pointsBetweenFromToInclusive(LEFT_X, LEFT_Y, BOTTOM_LEFT_X, BOTTOM_LEFT_Y,
					unitCount / 4);

				// drop units
				// top to mid from both sides in parallel
				for (int i = 0; i < topToRightPoints.length; i++) {
					int[] topRightPoint = topToRightPoints[i];
					RobotUtils.leftClick(topRightPoint[0], topRightPoint[1], PAUSE_BETWEEN_UNIT_DROP);

					if (i < topToLeftPoints.length) {
						int[] topLeftPoint = topToLeftPoints[i];
						RobotUtils.leftClick(topLeftPoint[0], topLeftPoint[1], PAUSE_BETWEEN_UNIT_DROP);
					}
				}
				
				// select unit
				RobotUtils.leftClick(Clickable.getButtonAttackUnit(unitIdx + 1), 100);
				// mid to bottom from both sides in parallel
				for (int i = 0; i < rightToBottomPoints.length; i++) {
					int[] rightToBottomPoint = rightToBottomPoints[i];
					RobotUtils.leftClick(rightToBottomPoint[0], rightToBottomPoint[1], PAUSE_BETWEEN_UNIT_DROP);

					int[] leftToBottomPoint = leftToBottomPoints[i];
					RobotUtils.leftClick(leftToBottomPoint[0], leftToBottomPoint[1], PAUSE_BETWEEN_UNIT_DROP);
				}
			}
		}
	}
}
