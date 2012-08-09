/**
 *
 */
package de.piratech.mapimap.service.meetingcollector.html;

import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.TagNode;

import de.piratech.mapimap.data.Meeting;
import de.piratech.mapimap.data.MeetingFactory;
import de.piratech.mapimap.service.Geocoder;

/**
 * @author maria
 * 
 */
public class HTMLMeetingCollectorLinkList extends HTMLAttributeMeetingCollector {

	private String base;

	public HTMLMeetingCollectorLinkList(HTMLSource parameterObject,
			Geocoder _geocoder, String _base, MeetingFactory<?> meetingFactory) {
		super(parameterObject, _geocoder, meetingFactory);
		this.base = _base;
	}

	@Override
	public List<Meeting> getMeetings() {
		List<TagNode> linkList = getMeetingTagNodes();
		List<Meeting> meetings = new ArrayList<Meeting>();
		for (TagNode link : linkList) {
			String href = this.base + link.getAttributeByName("href");
			Meeting meeting = getMeeting(getTagNode(href));
			if (meeting != null) {
				meeting.setWikiUrl(href);
				meetings.add(meeting);
			}
		}
		return meetings;
	}

}
