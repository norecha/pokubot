package aok.coc.state;

public class StateNoAction implements State {

	private final static StateNoAction instance = new StateNoAction();
	
	private StateNoAction() {
	}
	
	@Override
	public void handle(Context context) {
		System.out.println("StateNoAction");
	}

	public static StateNoAction instance() {
		return instance;
	}
}
