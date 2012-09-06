package de.piratech.mapimap.service.meetingcollector.html;

import java.util.List;

import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.piratech.mapimap.data.source.SourceInformationIdentifierType;

public class HTMLAttributeMeetingCollector extends AbstractHTMLMeetingCollector {

	private static final Logger LOG = LoggerFactory
			.getLogger(HTMLAttributeMeetingCollector.class);

	@Override
	protected List<TagNode> getMeetingTagNodes() {
		return getNodesWithAttribute(getTagNode(),
				this.htmlSource.getSourceInformationIdentifier(
						SourceInformationIdentifierType.MEETING, AttributeMatcher.class));
	}

	@Override
	protected String getLon(TagNode meeting) {
		AttributeMatcher attributeMatcher = getAttributeMatcher(SourceInformationIdentifierType.LON);
		if (attributeMatcher != null) {
			return getNodeValue(getNodeWithAttribute(meeting, attributeMatcher));
		} else {
			return null;
		}
	}

	@Override
	protected String getLat(TagNode meeting) {
		AttributeMatcher attributeMatcher = getAttributeMatcher(SourceInformationIdentifierType.LAT);
		if (attributeMatcher != null) {
			return getNodeValue(getNodeWithAttribute(meeting, attributeMatcher));
		} else {
			return null;
		}
	}

	@Override
	protected String getRoad(TagNode meeting) {
		AttributeMatcher attributeMatcher = getAttributeMatcher(SourceInformationIdentifierType.STREET);
		if (attributeMatcher != null) {
			return getNodeValue(getNodeWithAttribute(meeting, attributeMatcher));
		} else {
			return null;
		}
	}

	@Override
	protected String getZip(TagNode meeting) {
		AttributeMatcher attributeMatcher = getAttributeMatcher(SourceInformationIdentifierType.ZIP);
		if (attributeMatcher != null) {
			return getNodeValue(getNodeWithAttribute(meeting, attributeMatcher));
		} else {
			return null;
		}
	}

	@Override
	protected String getCity(TagNode meeting) {
		AttributeMatcher attributeMatcher = getAttributeMatcher(SourceInformationIdentifierType.TOWN);
		if (attributeMatcher != null) {
			return getNodeValue(getNodeWithAttribute(meeting, attributeMatcher));
		} else {
			return null;
		}
	}

	@Override
	protected String getAddress(TagNode meeting) {
		AttributeMatcher attributeMatcher = getAttributeMatcher(SourceInformationIdentifierType.ADDRESS);
		if (attributeMatcher != null) {
			return getNodeValue(getNodeWithAttribute(meeting, attributeMatcher));
		} else {
			LOG.warn("No Address defined for meeting");
			return null;
		}
	}

	private AttributeMatcher getAttributeMatcher(
			SourceInformationIdentifierType type) {
		return this.htmlSource.getSourceInformationIdentifier(type,
				AttributeMatcher.class);
	}

	@Override
	protected String getName(TagNode meetingNode) {
		return getNodeValue(getNodeWithAttribute(meetingNode,
				getAttributeMatcher(SourceInformationIdentifierType.NAME)));
	}

	@Override
	protected String getURL(TagNode meeting) {
		AttributeMatcher attributeMatcher = getAttributeMatcher(SourceInformationIdentifierType.URL);
		if (attributeMatcher != null) {
			TagNode node = getNodeWithAttribute(meeting, attributeMatcher);
			if (node != null) {
				TagNode link = (TagNode) node.getChildren().get(0);
				return link.getAttributeByName("href");
			}
		}
		return null;
	}

}
