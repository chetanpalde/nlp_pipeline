package gate.creole.morph; 

import java.util.ArrayList;
import java.util.List;

public class FSMState {
	private CharMap transitionFunction = new CharMap();
	public static final byte CHILD_STATE = 0;
	public static final byte ADJ_STATE = 1;
	private int index = 0;
	private List<RHS> rhses = new ArrayList<RHS>();
	
	public FSMState(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public FSMState next(char ch, byte type) {
		return transitionFunction.get(ch, type);
	}

	public void put(char chr, FSMState state, byte type) {
		transitionFunction.put(chr, state, type);
	}

	public List<RHS> getRHSes() {
		return rhses;
	}

	public void addRHS(RHS rhs) {
		if(!rhses.contains(rhs))
			rhses.add(rhs);
	}

	public CharMap getTransitionFunction() {
		return transitionFunction;
	}

}
