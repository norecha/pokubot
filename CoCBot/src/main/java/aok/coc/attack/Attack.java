package aok.coc.attack;

import aok.coc.util.ImageParser;

public abstract class Attack {
	
	public abstract void attack(int[] loot) throws InterruptedException;
	
	static int TOP_X = 429;
	static int TOP_Y = 44;
	
	static int LEFT_X = 70;
	static int LEFT_Y = 307;
	
	static int RIGHT_X = 775;
	static int RIGHT_Y = 307;

	static final int[][] pointsBetweenFromToInclusive(int fromX, int fromY, int toX, int toY, int count) {
		
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

	void checkLootChange(int[] loot) throws InterruptedException {
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
		}
	}
}
