package aok.coc.state;

import aok.coc.util.Clickable;
import aok.coc.util.ConfigUtils;
import aok.coc.util.ImageParser;
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
				
				int[] loot = ImageParser.parseLoot();

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
					ConfigUtils.instance().getAttackStrategy().attack(loot);
					
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
	
}
