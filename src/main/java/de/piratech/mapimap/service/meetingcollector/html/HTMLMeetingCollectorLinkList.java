/**
 *
 */
package de.piratech.mapimap.service.meetingcollector.html;

import java.util.List;

import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.piratech.mapimap.data.Meeting;
import de.piratech.mapimap.data.MeetingList;

/**
 * @author maria
 * 
 */
public class HTMLMeetingCollectorLinkList extends HTMLAttributeMeetingCollector {

	private static final Logger LOG = LoggerFactory
			.getLogger(HTMLMeetingCollectorLinkList.class);

	@Override
	public List<Meeting> getMeetings() {
		List<TagNode> linkList = getMeetingTagNodes();
		MeetingList meetings = new MeetingList();
		for (TagNode link : linkList) {
			String href = this.htmlSource.getBase() + link.getAttributeByName("href");
			Meeting meeting = getMeeting(getTagNode(href));
			if (meeting != null) {
				meeting.setWikiUrl(href);
				if (!meetings.add(meeting)) {
					LOG.warn("meeting {} was not added to meeting list",
							meeting.getName());
				} else {
					LOG.info("found meeting {} with address {}", meeting.getName(),
							meeting.getLocationData().getAddress().getAddressString());
				}
			}
		}
		return meetings;
	}
}
