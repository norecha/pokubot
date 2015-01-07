package aok.coc.state;

import aok.coc.util.Clickable;
import aok.coc.util.RobotUtils;

public class StateFindAMatch implements State {

	private static final StateFindAMatch instance = new StateFindAMatch();
	
	public static StateFindAMatch instance() {
		return instance;
	}

	private StateFindAMatch() {
	}

	@Override
	public void handle(Context context) {
		System.out.println("StateFindAMatch");
		if (RobotUtils.isClickableActive(Clickable.BUTTON_FIND_A_MATCH)) {
			try {
				RobotUtils.leftClick(Clickable.BUTTON_FIND_A_MATCH, 200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}

			context.setState(StateAttack.instance());
		} else {
			context.setState(StateIdle.instance());
		}
	}

}
