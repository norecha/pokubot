package aok.coc.state;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import aok.coc.util.Clickable;
import aok.coc.util.ConfigUtils;
import aok.coc.util.RobotUtils;


public class StateTrainTroops implements State {
	
	private static StateTrainTroops instance = new StateTrainTroops();
	
	private StateTrainTroops() {
	}

	@Override
	public void handle(Context context) {
		try {
			System.out.println("StateTrainTroops");
			
			List<Map<Clickable, Integer>> raxInfo = ConfigUtils.instance().getRaxInfo();
			for (int currRax = 0; currRax < raxInfo.size(); currRax++) {
				Map<Clickable, Integer> map = raxInfo.get(currRax);
				for (Entry<Clickable, Integer> entry : map.entrySet()) {
					Clickable troop = entry.getKey();
					int count = entry.getValue();
					
					for (int i = 0; i < count; i++) {
						RobotUtils.leftClick(troop, 75);
					}
				}
				
				if (currRax < raxInfo.size() - 1) {
					// select next rax
					RobotUtils.leftClick(Clickable.BUTTON_RAX_NEXT, 250);
				} else {
					RobotUtils.leftClick(Clickable.BUTTON_RAX_CLOSE, 250);
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
			try {
				RobotUtils.leftClick(Clickable.BUTTON_RAX_CLOSE, 200);
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
				e1.printStackTrace();
			}
			context.setState(StateNoAction.instance());
		}
	}

	public static StateTrainTroops instance() {
		return instance;
	}

}
