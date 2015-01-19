package aok.coc.attack;


public class ManualAttack extends AbstractAttack {

	private static final ManualAttack	instance	= new ManualAttack();

	public static ManualAttack instance() {
		return instance;
	}

	private ManualAttack() {
	}

	@Override
	protected void doDropUnits(int[] attackGroup) throws InterruptedException {
	}

}
