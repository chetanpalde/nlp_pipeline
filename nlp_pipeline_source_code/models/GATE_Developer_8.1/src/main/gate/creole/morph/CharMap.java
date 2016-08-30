package gate.creole.morph;

import java.util.Arrays;

public class CharMap {
	private char[] itemsKeys = null;

	private FSMState[] itemsObjs = null;

	private char[] adjitemsKeys = null;

	private FSMState[] adjitemsObjs = null;

	
	/**
	 * resize the containers by one leavaing empty elemant at position 'index'
	 */
	private void resizeCHILD(int index) {
		int newsz = itemsKeys.length + 1;
		char[] tempKeys = new char[newsz];
		FSMState[] tempObjs = new FSMState[newsz];

		int i;
		for (i = 0; i < index; i++) {
			tempKeys[i] = itemsKeys[i];
			tempObjs[i] = itemsObjs[i];
		}
		for (i = index + 1; i < newsz; i++) {
			tempKeys[i] = itemsKeys[i - 1];
			tempObjs[i] = itemsObjs[i - 1];
		}

		itemsKeys = tempKeys;
		itemsObjs = tempObjs;
	} // resize

	/**
	 * resize the containers by one leavaing empty elemant at position 'index'
	 */
	private void resizeADJ(int index) {
		int newsz = adjitemsKeys.length + 1;
		char[] tempKeys = new char[newsz];
		FSMState[] tempObjs = new FSMState[newsz];

		int i;
		for (i = 0; i < index; i++) {
			tempKeys[i] = adjitemsKeys[i];
			tempObjs[i] = adjitemsObjs[i];
		}
		for (i = index + 1; i < newsz; i++) {
			tempKeys[i] = adjitemsKeys[i - 1];
			tempObjs[i] = adjitemsObjs[i - 1];
		}

		adjitemsKeys = tempKeys;
		adjitemsObjs = tempObjs;
	} // resize

	/**
	 * get the object from the map using the char key
	 */
	public FSMState get(char key, byte type) {
		if(type == FSMState.CHILD_STATE) {
			if (itemsKeys == null)
				return null;
			int index = Arrays.binarySearch(itemsKeys, key);
			if (index < 0)
				return null;
			return itemsObjs[index];
		} else {
			if (adjitemsKeys == null)
				return null;
			int index = Arrays.binarySearch(adjitemsKeys, key);
			if (index < 0)
				return null;
			return adjitemsObjs[index];
		}
	}

	/**
	 * put the object into the char map using the chat as the key
	 */
	public FSMState put(char key, FSMState value, byte type) {
		if(type == FSMState.CHILD_STATE) {
			if (itemsKeys == null) {
				itemsKeys = new char[1];
				itemsKeys[0] = key;
				itemsObjs = new FSMState[1];
				itemsObjs[0] = value;
				return value;
			}// if first time
			int index = Arrays.binarySearch(itemsKeys, key);
			if (index < 0) {
				index = ~index;
				resizeCHILD(index);
				itemsKeys[index] = key;
				itemsObjs[index] = value;
			}
			return itemsObjs[index];
		} else {
			if (adjitemsKeys == null) {
				adjitemsKeys = new char[1];
				adjitemsKeys[0] = key;
				adjitemsObjs = new FSMState[1];
				adjitemsObjs[0] = value;
				return value;
			}// if first time
			int index = Arrays.binarySearch(adjitemsKeys, key);
			if (index < 0) {
				index = ~index;
				resizeADJ(index);
				adjitemsKeys[index] = key;
				adjitemsObjs[index] = value;
			}
			return adjitemsObjs[index];			
		}
	} // put

	public char[] getItemsKeys() {
		return itemsKeys;
	}

	public char[] getAdjitemsKeys() {
		return adjitemsKeys;
	}
}
