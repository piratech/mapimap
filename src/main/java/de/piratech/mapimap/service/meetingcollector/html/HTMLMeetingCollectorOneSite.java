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
public class HTMLMeetingCollectorOneSite<K extends Meeting> extends
		AbstractHTMLMeetingCollector<K> {

	public HTMLMeetingCollectorOneSite(HTMLSource htmlSource, Geocoder _geocoder,
			MeetingFactory<K> meetingFactory) {
		super(htmlSource, _geocoder, meetingFactory);
	}

	@Override
	public List<Meeting> getMeetings() {

		List<TagNode> nodesWithAttribute = getMeetingTagNodes();
		List<Meeting> crews = new ArrayList<Meeting>();
		for (TagNode tagNode : nodesWithAttribute) {
			K crew = getMeeting(tagNode);
			if (crew != null) {
				crews.add(crew);
			}
		}
		return crews;

	}
}
