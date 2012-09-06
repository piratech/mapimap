/**
 * 
 */
package de.piratech.mapimap.data;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author maria
 * 
 */
public class MeetingList extends ArrayList<Meeting> {

	private static final long serialVersionUID = -3362219700844041718L;

	public boolean add(Meeting newMeeting) {
		Iterator<Meeting> iterator = iterator();
		while (iterator.hasNext()) {
			Meeting meeting = (Meeting) iterator.next();
			if (meeting.getWikiUrl().equals(newMeeting.getWikiUrl())) {
				return false;
			}
		}
		return super.add(newMeeting);
	}
}
