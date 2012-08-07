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
public class HTMLMeetingCollectorOneSite extends AbstractHTMLMeetingCollector {

	public HTMLMeetingCollectorOneSite(HTMLSource htmlSource, Geocoder _geocoder,
			MeetingFactory<?> meetingFactory) {
		super(htmlSource, _geocoder, meetingFactory);
	}

	@Override
	public List<Meeting> getMeetings() {

		List<TagNode> meetingNodes = getMeetingTagNodes();
		List<Meeting> meetings = new ArrayList<Meeting>();
		for (TagNode meetingNode : meetingNodes) {
			Meeting meeting = getMeeting(meetingNode);
			if (meeting != null) {
				meetings.add(meeting);
			}
		}
		return meetings;

	}
}
