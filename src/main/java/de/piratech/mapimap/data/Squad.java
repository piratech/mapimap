/**
 * 
 */
package de.piratech.mapimap.data;

/**
 * @author maria
 * 
 */
public class Squad extends Meeting {

	// TODO think about using generics
	private DataType type = DataType.SQUAD;

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}
}
