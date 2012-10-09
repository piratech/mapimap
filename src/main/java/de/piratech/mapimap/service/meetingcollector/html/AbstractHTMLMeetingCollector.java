/**
 * 
 */
package de.piratech.mapimap.service.meetingcollector.html;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.piratech.mapimap.data.Address;
import de.piratech.mapimap.data.LocationData;
import de.piratech.mapimap.data.Meeting;
import de.piratech.mapimap.data.MeetingFactory;
import de.piratech.mapimap.data.MeetingList;
import de.piratech.mapimap.data.source.Source;
import de.piratech.mapimap.service.Geocoder;
import de.piratech.mapimap.service.meetingcollector.MeetingCollector;

/**
 * @author maria
 * 
 */
public abstract class AbstractHTMLMeetingCollector implements MeetingCollector {

	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractHTMLMeetingCollector.class);

	protected Geocoder geocoder;
	private HttpClient client;
	protected MeetingFactory<?> meetingFactory;
	protected Source htmlSource;

	public AbstractHTMLMeetingCollector() {
		this.client = new DefaultHttpClient();
	}

	@Override
	public void setGeocoder(Geocoder geocoder) {
		this.geocoder = geocoder;
	}

	@Override
	public void setMeetingFactory(MeetingFactory<Meeting> meetingFactory) {
		this.meetingFactory = meetingFactory;
	}

	@Override
	public void setSource(Source htmlSource) {
		this.htmlSource = htmlSource;
	}

	@Override
	public List<Meeting> getMeetings() {
		List<TagNode> meetingNodes = getMeetingTagNodes();
		MeetingList meetings = new MeetingList();
		for (TagNode meetingNode : meetingNodes) {
			Meeting meeting = getMeeting(meetingNode);
			if (meeting != null) {
				if (!meetings.add(meeting)) {
					LOG.warn("meeting >{}< not added to meeting list",
							meeting.getName());
				} else {
					LOG.info("found meeting {} with address {}",
							meeting.getName(), meeting.getLocationData()
									.getAddress().getAddressString());
				}
			}
		}
		return meetings;
	}

	protected abstract List<TagNode> getMeetingTagNodes();

	protected abstract String getName(TagNode meetingNode);

	protected abstract String getLon(TagNode meeting);

	protected abstract String getLat(TagNode meeting);

	protected abstract String getRoad(TagNode meeting);

	protected abstract String getZip(TagNode meeting);

	protected abstract String getCity(TagNode meeting);

	protected abstract String getAddress(TagNode meeting);

	protected abstract String getURL(TagNode meeting);

	private LocationData getLocationData(TagNode meeting) {
		String lonColumn = getLon(meeting);
		String latColumn = getLat(meeting);
		Address address = new Address();
		address.setRoad(getRoad(meeting));
		address.setPostcode(getZip(meeting));
		address.setCity(getCity(meeting));

		if (StringUtils.isEmpty(lonColumn) || StringUtils.isEmpty(latColumn)) {
			if (address.isValid()) {
				return geocoder.getLocationData(address.getAddressString());
			} else {
				String addressString = getAddress(meeting);
				if (StringUtils.isNotEmpty(addressString)) {
					return geocoder.getLocationData(addressString);
				} else {
					return null;
				}
			}
		} else {
			try {
				LocationData locationData = geocoder.getLocationData(
						Float.parseFloat(latColumn),
						Float.parseFloat(lonColumn));
				if (StringUtils.isEmpty(locationData.getAddress().getRoad())) {
					// sometimes streets are not returned
					String streetString = getRoad(meeting);
					if (StringUtils.isEmpty(streetString)) {
						String addressString = getAddress(meeting);
						if (!StringUtils.isEmpty(addressString)) {
							locationData.getAddress().setRoad(addressString);
						}
					} else {
						locationData.getAddress().setRoad(streetString);
					}
				}
				return locationData;
			} catch (NumberFormatException e) {
				LOG.error(e.getMessage(), e);
				return null;
			}
		}

	}

	protected TagNode getTagNode() {
		return getTagNode(this.htmlSource.getBase() + this.htmlSource.getUrl());
	}

	protected TagNode getTagNode(String href) {
		HttpGet get = new HttpGet(href);

		HttpResponse response;
		try {
			response = client.execute(get);
			// very old look for something newer (cannot use
			// org.w3c.dom.Document
			// because wiki sends invalid HTML and DomSerializer returns null)
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode tagNode = cleaner.clean(response.getEntity().getContent(),
					"UTF-8");
			return tagNode;
		} catch (ClientProtocolException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	protected TagNode getNodeWithAttribute(TagNode node,
			AttributeMatcher attributeMatcher) {
		List<TagNode> nodesWithAttribute = getNodesWithAttribute(node,
				attributeMatcher);
		if (!nodesWithAttribute.isEmpty()) {
			return nodesWithAttribute.get(0);
		}
		return null;
	}

	protected List<TagNode> getNodesWithAttribute(TagNode node,
			AttributeMatcher attributeMatcher) {
		List<TagNode> resultsList = new ArrayList<TagNode>();
		TagNode[] children = node.getAllElements(true);
		for (int i = 0; i < children.length; i++) {
			TagNode link = children[i];
			if (attributeMatcher.nodeHasAttribute(link)) {
				resultsList.add(link);
			}
		}
		return resultsList;
	}

	protected Meeting getMeeting(TagNode meetingNode) {
		Meeting meeting = meetingFactory.getInstance();
		meeting.setName(getName(meetingNode));
		String url = getURL(meetingNode);
		if (StringUtils.isNotEmpty(url) && !url.startsWith("http")) {
			meeting.setWikiUrl(this.htmlSource.getBase() + url);
		} else {
			meeting.setWikiUrl(url);
		}
		meeting.setForeignKey(meeting.getWikiUrl());

		LocationData locationData = getLocationData(meetingNode);
		if (locationData != null) {
			meeting.setLocationData(locationData);
			return meeting;
		} else {
			LOG.warn("skip meeting >{}< because location not found",
					meeting.getName());
			return null;
		}
	}

	protected String getNodeValue(TagNode node) {
		if (node != null) {
			return node.getText().toString();
		}
		return "";
	}

}
