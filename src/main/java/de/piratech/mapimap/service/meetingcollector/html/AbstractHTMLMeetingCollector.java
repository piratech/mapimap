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

import de.piratech.mapimap.data.LocationData;
import de.piratech.mapimap.data.Meeting;
import de.piratech.mapimap.data.MeetingFactory;
import de.piratech.mapimap.service.Geocoder;
import de.piratech.mapimap.service.meetingcollector.MeetingCollector;

/**
 * @author maria
 * 
 */
public abstract class AbstractHTMLMeetingCollector<K extends Meeting> implements
		MeetingCollector {

	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractHTMLMeetingCollector.class);

	private Geocoder geocoder;
	private HttpClient client;
	private MeetingFactory<K> meetingFactory;
	private HTMLSource htmlSource;

	public AbstractHTMLMeetingCollector(HTMLSource htmlsource, Geocoder _geocoder,
			MeetingFactory<K> meetingFactory) {
		this.htmlSource = htmlsource;
		this.geocoder = _geocoder;
		this.meetingFactory = meetingFactory;
		this.client = new DefaultHttpClient();
	}

	protected List<TagNode> getMeetingTagNodes() {
		List<TagNode> nodesWithAttribute = getNodesWithAttribute(getTagNode(),
				this.htmlSource.getMeetingIdentifier());
		return nodesWithAttribute;
	}

	private TagNode getTagNode() {
		return getTagNode(this.htmlSource.get_urlString());
	}

	protected TagNode getTagNode(String href) {
		HttpGet get = new HttpGet(href);

		HttpResponse response;
		try {
			response = client.execute(get);
			// very old look for something never (cannot use org.w3c.dom.Document
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

	private TagNode getNodeWithAttribute(TagNode node,
			AttributeMatcher attributeMatcher) {
		List<TagNode> nodesWithAttribute = getNodesWithAttribute(node,
				attributeMatcher);
		if (!nodesWithAttribute.isEmpty()) {
			return nodesWithAttribute.get(0);
		}
		return null;
	}

	private List<TagNode> getNodesWithAttribute(TagNode node,
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

	private String getMeetingAddress(final TagNode _tagNode) {
		String address = null;
		TagNode addressTag = _tagNode.findElementByAttValue(this.htmlSource
				.getAddressIdentifier().getAttributeName(), this.htmlSource
				.getAddressIdentifier().getAttributeValue(), true, true);
		if (addressTag != null) {
			address = addressTag.getText().toString();
		}
		return address;
	}

	protected K getMeeting(TagNode squadSite) {
		K crew = meetingFactory.getInstance();
		crew.setName(getMeetingName(squadSite));
		LOG.info("name: " + crew.getName());

		String address = getMeetingAddress((TagNode) squadSite);
		if (!StringUtils.isEmpty(address)) {
			LocationData locationData = geocoder.getLocationData(address);
			if (locationData != null) {
				crew.setLocationData(locationData);
				return crew;
			} else {
				LOG.warn("skip crew {} because location not found", crew.getName(),
						crew.getWikiUrl());
			}
		} else {
			LOG.warn("skip crew {} because address not found", crew.getName(),
					crew.getWikiUrl());
		}
		return null;
	}

	private String getMeetingName(TagNode squadSite) {
		TagNode nameNode = getNodeWithAttribute(squadSite,
				this.htmlSource.getNameIdentifier());
		if (nameNode != null) {
			return nameNode.getText().toString();
		}
		return "";
	}
}
