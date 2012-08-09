/**
 * 
 */
package de.piratech.mapimap.data;

/**
 * @author maria
 * 
 */
public class Stammtisch extends Meeting {

	private DataType type = DataType.STAMMTISCH;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.piratech.mapimap.data.Meeting#getType()
	 */
	@Override
	public DataType getType() {
		return this.type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.piratech.mapimap.data.Meeting#setType(de.piratech.mapimap.data.DataType)
	 */
	@Override
	public void setType(DataType type) {
		this.type = type;
	}

}
