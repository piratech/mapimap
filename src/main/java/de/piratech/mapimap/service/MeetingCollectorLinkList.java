/**
 *
 */
package de.piratech.mapimap.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.piratech.mapimap.data.LocationData;
import de.piratech.mapimap.data.Meeting;
import de.piratech.mapimap.data.Squad;

/**
 * @author maria
 * 
 */
public class MeetingCollectorLinkList implements MeetingCollector {

	public static final int EXACT_MATCH = 0;
	public static final int STARTS_WITH = 1;
	private static final int CONTAINT = 2;

	private static final Logger LOG = LoggerFactory
			.getLogger(MeetingCollectorLinkList.class);
	private Geocoder geocoder;
	private HttpClient client;
	private String linkListUrl;
	private String[] meetingIdentifier;
	private String[] nameIdentifier;
	private String[] addressIdentifier;

	public MeetingCollectorLinkList(final Geocoder _geocoder,
			String _linkListUrl, final String _meetingIdentifier,
			final String _nameIdentifier, final String _addressIdentifier) {
		this.geocoder = _geocoder;
		this.linkListUrl = _linkListUrl;
		this.meetingIdentifier = _meetingIdentifier.split(";");
		this.nameIdentifier = _nameIdentifier.split(":");
		this.addressIdentifier = _addressIdentifier.split(":");
		this.client = new DefaultHttpClient();
	}

	@Override
	public List<Meeting> getMeetings() {
		HttpGet get = new HttpGet(this.linkListUrl);
		try {

			HttpResponse response = client.execute(get);

			// very old look for something never (cannot use org.w3c.dom.Document
			// because wiki sends invalid HTML and DomSerializer returns null)
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode squadsSite = cleaner.clean(response.getEntity().getContent(),
					"UTF-8");

			List<TagNode> nodesWithAttribute = getNodesWithAttribute(

			meetingIdentifier[0], meetingIdentifier[1],
					Integer.parseInt(meetingIdentifier[2]), squadsSite);

			List<Meeting> crews = new ArrayList<Meeting>();
			for (TagNode tagNode : nodesWithAttribute) {
				String href = "http://wiki.piratenpartei.de"
						+ tagNode.getAttributeByName("href");
				response = client.execute(new HttpGet(href));
				TagNode squadSite = cleaner.clean(response.getEntity().getContent(),
						"UTF-8");
				TagNode nameNode = getNodeWithAttribute(nameIdentifier[0],
						nameIdentifier[1], Integer.parseInt(nameIdentifier[2]), squadSite);
				Squad crew = new Squad();
				crew.setWikiUrl(href);
				if (nameNode != null) {
					crew.setName(nameNode.getText().toString());
					LOG.info("name: " + crew.getName());
				}
				String address = getAddress((TagNode) squadSite);
				if (!StringUtils.isEmpty(address)) {
					LocationData locationData = geocoder.getLocationData(address);
					if (locationData != null) {
						crew.setLocationData(locationData);
						crews.add(crew);
					} else {
						LOG.warn("skip crew {} (url: {}) because location not found",
								crew.getName(), crew.getWikiUrl());
					}
				} else {
					LOG.warn("skip crew {} (url: {}) because address not found",
							crew.getName(), crew.getWikiUrl());
				}
			}
			return crews;
		} catch (IOException e) {
			LOG.error("message: {}", e);
		} catch (IllegalStateException e) {
			LOG.error("message: {}", e);
		}
		return null;
	}

	private TagNode getNodeWithAttribute(String attributeName,
			String attributeValue, int startsWith, TagNode node) {
		List<TagNode> nodesWithAttribute = getNodesWithAttribute(attributeName,
				attributeValue, startsWith, node);
		if (!nodesWithAttribute.isEmpty()) {
			return nodesWithAttribute.get(0);
		}
		return null;
	}

	private List<TagNode> getNodesWithAttribute(String attributeName,
			String attributeValue, int startsWith, TagNode node) {
		List<TagNode> resultsList = new ArrayList<TagNode>();
		TagNode[] children = node.getAllElements(true);
		for (int i = 0; i < children.length; i++) {
			TagNode link = children[i];
			if (hasAttribute(link, attributeName, attributeValue, startsWith)) {
				resultsList.add(link);
			}
		}
		return resultsList;
	}

	private boolean hasAttribute(TagNode link, String attributeName,
			String attributeValue, int searchMode) {
		return stringMatches(link.getAttributeByName(attributeName),
				attributeValue, searchMode);

	}

	private boolean stringMatches(String value, String matchValue, int macthMode) {
		switch (macthMode) {
		case EXACT_MATCH:
			return StringUtils.equals(value, matchValue);
		case STARTS_WITH:
			return StringUtils.startsWith(value, matchValue);
		case CONTAINT:
			return StringUtils.contains(value, matchValue);
		default:
			throw new IllegalArgumentException("match mode >" + macthMode
					+ "< not handled");
		}
	}

	private String getAddress(final TagNode _tagNode) {
		String address = null;
		TagNode addressTag = _tagNode.findElementByAttValue(addressIdentifier[0],
				addressIdentifier[1], true, true);
		if (addressTag != null) {
			address = addressTag.getText().toString();
		}
		return address;
	}
}
