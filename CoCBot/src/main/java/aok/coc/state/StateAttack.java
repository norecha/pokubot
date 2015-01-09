package aok.coc.state;

import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import aok.coc.util.ConfigUtils;
import aok.coc.util.ImageParser;
import aok.coc.util.RobotUtils;
import aok.coc.util.coords.Clickable;

public class StateAttack implements State {
	private static final Logger			logger		= Logger.getLogger(StateAttack.class.getName());

	private static final StateAttack	instance	= new StateAttack();

	public static StateAttack instance() {
		return instance;
	}

	private StateAttack() {
	}

	@Override
	public void handle(Context context) throws InterruptedException {
		while (true) {
			try {
				logger.info("StateAttack");
				if (Thread.interrupted()) {
					throw new InterruptedException("StateAttack is interrupted.");
				}

				int[] loot = ImageParser.parseLoot();
				int[] attackGroup = ImageParser.parseTroopCount();

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

					// attack or let user manually attack
					if (ConfigUtils.instance().isAutoAttackEnabled()) {
						playAttackReady();
						ConfigUtils.instance().getAttackStrategy().attack(loot, attackGroup);
						RobotUtils.leftClick(Clickable.BUTTON_END_BATTLE, 1200);
						RobotUtils.leftClick(Clickable.BUTTON_END_BATTLE_QUESTION_OKAY, 1200);
						RobotUtils.leftClick(Clickable.BUTTON_END_BATTLE_RETURN_HOME, 1200);
					} else {
						playAttackReady();
					}

					context.setState(StateIdle.instance());

					break;
				} else {
					// next
					RobotUtils.leftClick(Clickable.BUTTON_NEXT, 100);
					RobotUtils.sleepTillClickableIsActive(Clickable.BUTTON_NEXT);
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
				throw e;
			}
		}
	}

	private void playAttackReady() {
		String[] clips = new String[] { "/fight.wav", "/finishim.wav"};
		try (Clip clip = AudioSystem.getClip();
				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
					this.getClass().getResourceAsStream(clips[RobotUtils.random.nextInt(clips.length)]))) {

			clip.open(audioInputStream);
			clip.start();
			Thread.sleep(2000);
		} catch (Exception ex)
		{
		}
	}

}
