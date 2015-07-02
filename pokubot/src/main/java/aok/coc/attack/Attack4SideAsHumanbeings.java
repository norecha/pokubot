package aok.coc.attack;

import java.util.Random;
import java.util.logging.Logger;

import aok.coc.util.ImageParser;
import aok.coc.util.RobotUtils;
import aok.coc.util.coords.Clickable;

public class Attack4SideAsHumanbeings extends AbstractAttack {

	private static final Logger logger = Logger
			.getLogger(Attack4SideAsHumanbeings.class.getName());

	private static final Attack4SideAsHumanbeings instance = new Attack4SideAsHumanbeings();
	
	private Random randGen = null;

	public static Attack4SideAsHumanbeings instance() {
		return instance;
	}

	private Attack4SideAsHumanbeings() {
		randGen = new Random();
	}

	@Override
	protected void doDropUnits(int[] attackGroup) throws InterruptedException {
		logger.info("Dropping units from 4 sides, simulate human's behaviors.");

		// count troop
		int[] troopCounts = ImageParser.parseTroopCount();
		int troopCountsSum = 0;
		for (int troopCount : troopCounts) {
			troopCountsSum += troopCount;
		}

		// determine initial endpoints
		int startPointX = TOP_X;
		int startPointY = TOP_Y;
		int endPointX = LEFT_X;
		int endPointY = LEFT_Y;

		// wave by wave
		while (troopCountsSum > 0) {

			// rotate endpoints, attack from different sides
			int sideMagicNumber = randGen.nextInt(4);
			if (sideMagicNumber == 0) {
				startPointX = TOP_X;
				startPointY = TOP_Y;
				endPointX = RIGHT_X;
				endPointY = RIGHT_Y;
			} else if (sideMagicNumber == 1) {
				startPointX = RIGHT_X;
				startPointY = RIGHT_Y;
				endPointX = BOTTOM_RIGHT_X;
				endPointY = BOTTOM_RIGHT_Y;
			} else if (sideMagicNumber == 2) {
				startPointX = LEFT_X;
				startPointY = LEFT_Y;
				endPointX = BOTTOM_LEFT_X;
				endPointY = BOTTOM_LEFT_Y;
			} else if (sideMagicNumber == 3) {
				startPointX = TOP_X;
				startPointY = TOP_Y;
				endPointX = LEFT_X;
				endPointY = LEFT_Y;
			}

			int waveCount = WAVE_PER_SIDE;
			waveCount = randGen.nextInt(2)+1;
			//initiate WAVE_PER_SIDE waves for each side
			for (int wave = 0; wave < waveCount; wave++) {
				
				//determine attack line
				double biasRatio = randGen.nextDouble();		//between two ends
				double centralizeRatio = randGen.nextDouble()*0.15;	//to the centre
				int startX = (int)(startPointX + (endPointX - startPointX) * biasRatio);
				int startY = (int)(startPointY + (endPointY - startPointY) * biasRatio);
				startX = (int)(startX + (CENTRE_X - startX) * centralizeRatio);
				startY = (int)(startY + (CENTRE_Y - startY) * centralizeRatio);
				
				biasRatio = randGen.nextDouble();		//between two ends
				centralizeRatio = randGen.nextDouble()*0.15;	//to the centre
				int endX = (int)(startPointX + (endPointX - startPointX) * biasRatio);
				int endY = (int)(startPointY + (endPointY - startPointY) * biasRatio);
				endX = (int)(endX + (CENTRE_X - endX) * centralizeRatio);
				endY = (int)(endY + (CENTRE_Y - endY) * centralizeRatio);
				
				//start attacking
				for (int unitIdx = 0; unitIdx < attackGroup.length; unitIdx++) {
					int unitCount = attackGroup[unitIdx];

					// select unit
					RobotUtils.leftClick(
							Clickable.getButtonAttackUnit(unitIdx + 1), 200+randGen.nextInt(500));

					int[][] attackPoints = pointsBetweenFromToInclusive(
							startX, startY, endX, endY, (int)Math.ceil(unitCount / 12.0 * (1+randGen.nextDouble())), DEFAULT_DISTURB);

					// drop units
					for (int[] point : attackPoints) {
						RobotUtils.leftClick(point[0], point[1],
								PAUSE_BETWEEN_UNIT_DROP+39+randGen.nextInt(200));
					}
				}
				
			}

			troopCounts = ImageParser.parseTroopCount();
			troopCountsSum = 0;
			for (int troopCount : troopCounts) {
				troopCountsSum += troopCount;
			}
		}
	}
}
