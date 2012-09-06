/**
 * 
 */
package de.piratech.mapimap.data;

/**
 * @author maria
 * 
 */
public class Stammtisch extends Meeting {

	private MeetingType type = MeetingType.STAMMTISCH;

	@Override
	public MeetingType getType() {
		return this.type;
	}

	@Override
	public void setType(MeetingType type) {
		this.type = type;
	}

}
