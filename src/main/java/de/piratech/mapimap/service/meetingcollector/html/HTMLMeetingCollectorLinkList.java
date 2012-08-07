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
public class HTMLMeetingCollectorLinkList<K extends Meeting> extends
		AbstractHTMLMeetingCollector<K> {

	private String base;

	public HTMLMeetingCollectorLinkList(HTMLSource parameterObject,
			Geocoder _geocoder, String _base, MeetingFactory<K> meetingFactory) {
		super(parameterObject, _geocoder, meetingFactory);
		this.base = _base;
	}

	@Override
	public List<Meeting> getMeetings() {

		List<TagNode> nodesWithAttribute = getMeetingTagNodes();

		List<Meeting> crews = new ArrayList<Meeting>();
		for (TagNode tagNode : nodesWithAttribute) {
			String href = this.base + tagNode.getAttributeByName("href");
			Meeting meeting = getMeeting(getTagNode(href));
			if (meeting != null) {
				meeting.setWikiUrl(href);
				crews.add(meeting);
			}
		}
		return crews;
	}

}
