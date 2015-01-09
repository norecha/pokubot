package aok.coc.state;

import java.util.List;
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
		try {
			logger.info("StateTrainTroops");
			// first barracks must be opened at this point
			
			List<Clickable> raxInfo = ConfigUtils.instance().getRaxInfo();
			for (int currRax = 0; currRax < raxInfo.size(); currRax++) {
				Clickable troop = raxInfo.get(currRax);
				for (int i = 0; i < RobotUtils.random.nextInt(10) + 5; i++) {
					RobotUtils.leftClick(troop, 75);
				}
				
				if (currRax < raxInfo.size() - 1) {
					// select next rax
					RobotUtils.leftClick(Clickable.BUTTON_RAX_NEXT, 250);
				} else {
					RobotUtils.leftClick(Clickable.BUTTON_RAX_CLOSE, 250);
				}
			}
		} catch (InterruptedException e) {
			RobotUtils.leftClick(Clickable.BUTTON_RAX_CLOSE, 200);
			throw e;
		}
		
		context.setState(StateMainMenu.instance());
		Thread.sleep(3000 + RobotUtils.random.nextInt(3000));
	}

	public static StateTrainTroops instance() {
		return instance;
	}

}
