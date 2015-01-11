package aok.coc.util.coords;

import java.awt.Color;

public enum Clickable {
	
	BUTTON_ATTACK(61, 638, new Color(0xFFDB6458)),
	BUTTON_FIND_A_MATCH(218, 544, new Color(0xFF2F1300)),
	BUTTON_NEXT(824, 517, new Color(0xFFEDAC28)),
	BUTTON_END_BATTLE(63, 547, new Color(0xE25050)),
	BUTTON_END_BATTLE_QUESTION_OKAY(509, 394, null),
	BUTTON_END_BATTLE_RETURN_HOME(436, 536, null),
	BUTTON_RAX_NEXT(767, 361, new Color(0xF8B046)),
	BUTTON_RAX_BARB(212, 324, new Color(0xF8B720)),
	BUTTON_RAX_ARCHER(331, 333, new Color(0xE54070)),
	BUTTON_RAX_FULL(155, 536, new Color(0xD0404C)),
	BUTTON_RAX_TRAIN(621, 626, new Color(0x708CB0)),
	BUTTON_RAX_CLOSE(730, 150, new Color(0xF8FCFF)),
	BUTTON_ATTACK_UNIT_1(72, 633, null),
	BUTTON_ATTACK_UNIT_2(145, 633, null),
	BUTTON_ATTACK_UNIT_3(217, 633, null),
	BUTTON_ATTACK_UNIT_4(288, 633, null),
	BUTTON_ATTACK_UNIT_5(361, 633, null),
	BUTTON_WALL_UPGRADE(519, 619, new Color(0xD8CCCE)),
	UNIT_FIRST_RAX(null, null, null),
	UNIT_BLUESTACKS_DC(633, 315, new Color(0x282828)), 
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
