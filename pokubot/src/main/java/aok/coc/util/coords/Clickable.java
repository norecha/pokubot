package aok.coc.util.coords;

import java.awt.Color;

/**
 * All coordinates are client coordinates
 * @author norecha
 */
public enum Clickable {
	
	BUTTON_ATTACK(86, 623, new Color(0xA24717)),
	BUTTON_FIND_A_MATCH(148, 529, new Color(0xD84B00)),
	BUTTON_SHIELD_DISABLE(512, 397, null),
	BUTTON_NEXT(816, 484, new Color(0xECAC28)),
	BUTTON_END_BATTLE(59, 517, null),
	BUTTON_END_BATTLE_QUESTION_OKAY(509, 394, null),
	BUTTON_END_BATTLE_RETURN_HOME(436, 536, null),
	BUTTON_RAX_NEXT(767, 361, new Color(0xF8B046)),
	BUTTON_RAX_BARB(212, 324, null),
	BUTTON_RAX_ARCHER(331, 333, null),
	BUTTON_RAX_FULL(156, 507, new Color(0xD04048)),
	BUTTON_RAX_TRAIN(614, 584, new Color(0xF8F4F8)),
	BUTTON_RAX_CLOSE(729, 145, new Color(0xF8FCFF)),
	BUTTON_ATTACK_UNIT_1(72, 600, null),
	BUTTON_ATTACK_UNIT_2(145, 600, null),
	BUTTON_ATTACK_UNIT_3(217, 600, null),
	BUTTON_ATTACK_UNIT_4(288, 600, null),
	BUTTON_ATTACK_UNIT_5(361, 600, null),
	UNIT_FIRST_RAX(null, null, null),
	UNIT_BLUESTACKS_DC(699, 343, new Color(0x282828)), // 160,250 to 700,420
	UNIT_RECONNECT(435, 400, null);
	
	private Integer	x;
	private Integer y;
	private Color color;

	private Clickable(Integer x, Integer y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	public static Clickable getButtonAttackUnit(int x) {
		switch (x) {
		case 1:
			return BUTTON_ATTACK_UNIT_1;
		case 2:
			return BUTTON_ATTACK_UNIT_2;
		default:
			throw new IllegalArgumentException(x + "");
		}
	}

	public Integer getX() {
		return x;
	}

	public Integer getY() {
		return y;
	}

	public Color getColor() {
		return color;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public void setY(Integer y) {
		this.y = y;
	}

}
