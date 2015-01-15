package aok.coc.state;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import aok.coc.exception.BotException;
import aok.coc.util.ConfigUtils;
import aok.coc.util.ImageParser;
import aok.coc.util.RobotUtils;
import aok.coc.util.coords.Area;
import aok.coc.util.coords.Clickable;

public class StateAttack implements State {
	private static final Logger			logger		= Logger.getLogger(StateAttack.class.getName());

	private static final StateAttack	instance	= new StateAttack();

	public static StateAttack instance() {
		return instance;
	}

	private int[]	prevLoot;

	private StateAttack() {
	}

	@Override
	public void handle(Context context) throws InterruptedException, BotException {
		while (true) {
			logger.info("StateAttack");
			if (Thread.interrupted()) {
				throw new InterruptedException("StateAttack is interrupted.");
			}

			int[] loot = ImageParser.parseLoot();
			int[] attackGroup = ImageParser.parseTroopCount();

			int gold = loot[0];
			int elixir = loot[1];
			int de = loot[2];

			try {
				ImageParser.parseCollectorBase();
				RobotUtils.saveScreenShot("attack_"+System.currentTimeMillis(), Area.ENEMY_BASE);
				Thread.sleep(3000);
			} catch (IOException e) {
				throw new BotException("",e);
			}
			
			if (!ConfigUtils.instance().doConditionsMatch(gold, elixir, de)) {

				// attack or let user manually attack
				if (ConfigUtils.instance().isAutoAttackEnabled()) {
					playAttackReady();
					ConfigUtils.instance().getAttackStrategy().attack(loot, attackGroup);
					RobotUtils.leftClick(Clickable.BUTTON_END_BATTLE, 1200);
					RobotUtils.leftClick(Clickable.BUTTON_END_BATTLE_QUESTION_OKAY, 1200);
					RobotUtils.leftClick(Clickable.BUTTON_END_BATTLE_RETURN_HOME, 1200);
				} else {
					if (Arrays.equals(prevLoot, loot)) {
						logger.info("User is manually attacking/deciding.");
					} else {
						playAttackReady();
					}
					prevLoot = loot;

					/**
					 * NOTE: minor race condition
					 * 1. Matching base found.
					 * 2. sound is played.
					 * 3. prevLoot is set to full available loot
					 * 4. Thread.sleep(XXX)
					 * 5. StateIdle -> next is available to state is set back to attack.
					 * 6. user drops units, loot number changes AFTER state is set BEFORE this state parsed the image.
					 * 7. loot is different now.
					 * 8. sound is played again which is wrong.
					 * 9. won't happen more than once since next button won't be available after attack has started.
					 */
					Thread.sleep(5000);
				}

				context.setState(StateIdle.instance());

				break;
			} else {
				// next
				RobotUtils.leftClick(Clickable.BUTTON_NEXT, 100);
				RobotUtils.sleepTillClickableIsActive(Clickable.BUTTON_NEXT);

				// to avoid server/client sync from nexting too fast
				RobotUtils.sleepRandom(350);
			}
		}
	}

	private void playAttackReady() {
		String[] clips = new String[] { "/fight.wav", "/finishim.wav" };
		try (Clip clip = AudioSystem.getClip();
				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
					this.getClass().getResourceAsStream(clips[RobotUtils.random.nextInt(clips.length)]))) {

			clip.open(audioInputStream);
			clip.start();
			Thread.sleep(2000);
		} catch (Exception ex) {
			logger.log(Level.WARNING, "Unable to play audio.", ex);
		}
	}

}
