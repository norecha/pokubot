package aok.coc.state;

import java.util.logging.Logger;

import aok.coc.util.ConfigUtils;
import aok.coc.util.RobotUtils;
import aok.coc.util.coords.Clickable;


public class StateTrainTroops implements State {
	private static final Logger	logger	= Logger.getLogger(StateTrainTroops.class.getName());
	
	private static StateTrainTroops instance = new StateTrainTroops();
	
	private StateTrainTroops() {
	}

	@Override
	public void handle(Context context) throws InterruptedException {
		logger.info("StateTrainTroops");
		// first barracks must be opened at this point
		
		Clickable[] raxInfo = ConfigUtils.instance().getRaxInfo();
		for (int currRax = 0; currRax < raxInfo.length; currRax++) {
			Clickable troop = raxInfo[currRax];
			
			if (troop != Clickable.BUTTON_RAX_NO_UNIT) {
				for (int i = 0; i < RobotUtils.random.nextInt(5) + 15; i++) {
					RobotUtils.leftClick(troop, 75);
				}
			}
			
			if (currRax < raxInfo.length - 1) {
				// select next rax
				RobotUtils.leftClick(Clickable.BUTTON_RAX_NEXT, 350);
			}
		}
		RobotUtils.leftClick(Clickable.BUTTON_RAX_CLOSE, 250);
		
		context.setState(StateMainMenu.instance());
		RobotUtils.sleepRandom(5000);
	}

	public static StateTrainTroops instance() {
		return instance;
	}

}
