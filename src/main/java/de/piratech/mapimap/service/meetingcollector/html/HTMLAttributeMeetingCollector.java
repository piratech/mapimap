package de.piratech.mapimap.service.meetingcollector.html;

import java.util.List;

import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.piratech.mapimap.data.MeetingFactory;
import de.piratech.mapimap.service.Geocoder;

public class HTMLAttributeMeetingCollector extends AbstractHTMLMeetingCollector {

	private static final Logger LOG = LoggerFactory
			.getLogger(HTMLAttributeMeetingCollector.class);

	public HTMLAttributeMeetingCollector(HTMLSource htmlsource,
			Geocoder _geocoder, MeetingFactory<?> meetingFactory) {
		super(htmlsource, _geocoder, meetingFactory);
	}

	@Override
	protected List<TagNode> getMeetingTagNodes() {
		return getNodesWithAttribute(getTagNode(),
				this.htmlSource.getAttributeIdentifier(HTMLSource.MEETING_TAG));
	}

	@Override
	protected String getLon(TagNode meeting) {
		AttributeMatcher attributeMatcher = getAttributeMatcher(HTMLSource.LON_TAG);
		if (attributeMatcher != null) {
			return getNodeValue(getNodeWithAttribute(meeting, attributeMatcher));
		} else {
			return null;
		}
	}

	@Override
	protected String getLat(TagNode meeting) {
		AttributeMatcher attributeMatcher = getAttributeMatcher(HTMLSource.LAT_TAG);
		if (attributeMatcher != null) {
			return getNodeValue(getNodeWithAttribute(meeting, attributeMatcher));
		} else {
			return null;
		}
	}

	@Override
	protected String getRoad(TagNode meeting) {
		AttributeMatcher attributeMatcher = getAttributeMatcher(HTMLSource.STREET_TAG);
		if (attributeMatcher != null) {
			return getNodeValue(getNodeWithAttribute(meeting, attributeMatcher));
		} else {
			return null;
		}
	}

	@Override
	protected String getZip(TagNode meeting) {
		AttributeMatcher attributeMatcher = getAttributeMatcher(HTMLSource.ZIP_TAG);
		if (attributeMatcher != null) {
			return getNodeValue(getNodeWithAttribute(meeting, attributeMatcher));
		} else {
			return null;
		}
	}

	@Override
	protected String getCity(TagNode meeting) {
		AttributeMatcher attributeMatcher = getAttributeMatcher(HTMLSource.TOWN_TAG);
		if (attributeMatcher != null) {
			return getNodeValue(getNodeWithAttribute(meeting, attributeMatcher));
		} else {
			return null;
		}
	}

	@Override
	protected String getAddress(TagNode meeting) {
		AttributeMatcher attributeMatcher = getAttributeMatcher(HTMLSource.ADDRESS_TAG);
		if (attributeMatcher != null) {
			return getNodeValue(getNodeWithAttribute(meeting, attributeMatcher));
		} else {
			LOG.warn("No Address defined for meeting");
			return null;
		}
	}

	private AttributeMatcher getAttributeMatcher(String attribute) {
		return this.htmlSource.getAttributeIdentifier(attribute);
	}

	@Override
	protected String getName(TagNode meetingNode) {
		return getNodeValue(getNodeWithAttribute(meetingNode,
				getAttributeMatcher(HTMLSource.NAME_TAG)));
	}

	@Override
	protected String getURL(TagNode meeting) {
		AttributeMatcher attributeMatcher = getAttributeMatcher(HTMLSource.URL_TAG);
		if (attributeMatcher != null) {
			TagNode link = getNodeWithAttribute(meeting, attributeMatcher);
			return link.getAttributeByName("href");
		}
		return null;
	}

}
