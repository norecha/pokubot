package aok.coc.state;

import java.util.Map;

import aok.coc.util.Clickable;
import aok.coc.util.ConfigUtils;
import aok.coc.util.RobotUtils;

public class StateAttack implements State {

	private static final StateAttack	instance	= new StateAttack();

	public static StateAttack instance() {
		return instance;
	}

	private StateAttack() {
	}

	@Override
	public void handle(Context context) {
		while (true) {
			try {
				System.out.println("StateAttack");
				// attack();
				if (Thread.interrupted()) {
					throw new InterruptedException("StateAttack is interrupted.");
				}
				
				int[] loot = RobotUtils.parseLoot();

				int gold = loot[0];
				int elixir = loot[1];
				int de = loot[2];
				
				if ((ConfigUtils.instance().isMatchAllConditions() &&
					gold >= ConfigUtils.instance().getGoldThreshold() &&
					elixir >= ConfigUtils.instance().getElixirThreshold() &&
					de >= ConfigUtils.instance().getDarkElixirThreshold())
					||
					(!ConfigUtils.instance().isMatchAllConditions() &&
					(gold >= ConfigUtils.instance().getGoldThreshold() ||
					elixir >= ConfigUtils.instance().getElixirThreshold() ||
					de >= ConfigUtils.instance().getDarkElixirThreshold()))) {
					
					// attack
					attack(loot);
					
					RobotUtils.leftClick(Clickable.BUTTON_END_BATTLE, 1200);
					RobotUtils.leftClick(Clickable.BUTTON_END_BATTLE_QUESTION_OKAY, 1200);
					RobotUtils.leftClick(Clickable.BUTTON_END_BATTLE_QUESTION_OKAY, 1200);
					RobotUtils.leftClick(Clickable.BUTTON_END_BATTLE_RETURN_HOME, 1200);
					
					context.setState(StateIdle.instance());
					
					break;
				} else {
					// next
					RobotUtils.leftClick(Clickable.BUTTON_NEXT, 750);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
				try {
					RobotUtils.leftClick(Clickable.BUTTON_END_BATTLE, 500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				// TODO: click yes if attack has already started
				
				context.setState(StateIdle.instance());
			}
		}
	}
	
	private static int TOP_X = 429;
	private static int TOP_Y = 44;
	
	private static int LEFT_X = 70;
	private static int LEFT_Y = 307;
	
	private static int RIGHT_X = 775;
	private static int RIGHT_Y = 307;
	
	private void attack(int[] loot) throws InterruptedException {
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

	private void checkLootChange(int[] loot) throws InterruptedException {
		int[] prevLoot = loot;
		int diff = Integer.MAX_VALUE;
		int delta = 500;
		while (diff > delta) {
			Thread.sleep(10000);

			int[] currLoot = RobotUtils.parseLoot();

			diff = 0;
			for (int i = 0; i < prevLoot.length; i++) {
				diff += prevLoot[i] - currLoot[i];
			}
		}
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

	private static int[][] pointsBetweenFromToInclusive(int fromX, int fromY, int toX, int toY, int count) {
		
		if (count < 2) {
			throw new IllegalArgumentException(count + "");
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
	
}
