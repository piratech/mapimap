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

import de.piratech.mapimap.data.Crew;
import de.piratech.mapimap.data.LocationData;
import de.piratech.mapimap.data.Meeting;

/**
 * @author maria
 * 
 */
public class MeetingCollectorOneSite implements MeetingCollector {

	private static final Logger LOG = LoggerFactory
			.getLogger(MeetingCollectorOneSite.class);
	private Geocoder geocoder;
	private HttpClient client;
	private String urlString;
	private String[] meetingIdentifier;
	private String[] nameIdentifier;
	private String[] addressIdentifier;

	public MeetingCollectorOneSite(final Geocoder _geocoder,
			final String _urlString, final String _meetingIdentifier,
			final String _nameIdentifier, final String _addressIdentifier) {
		this.geocoder = _geocoder;
		this.urlString = _urlString;
		this.meetingIdentifier = _meetingIdentifier.split(":");
		this.nameIdentifier = _nameIdentifier.split(":");
		this.addressIdentifier = _addressIdentifier.split(":");
		this.client = new DefaultHttpClient();
	}

	@Override
	public List<Meeting> getMeetings() {
		HttpGet get = new HttpGet(this.urlString);
		try {

			HttpResponse response = client.execute(get);

			// very old look for something never (cannot use org.w3c.dom.Document
			// because wiki sends invalid HTML)
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode node = cleaner.clean(response.getEntity().getContent(), "UTF-8");
			List<?> elementListByAttValue = node.getElementListByAttValue(
					this.meetingIdentifier[0], this.meetingIdentifier[1], true, true);
			List<Meeting> crews = new ArrayList<Meeting>();
			for (Object tagNode : elementListByAttValue) {
				Crew crew = new Crew();
				crew.setName(((TagNode) tagNode)
						.findElementByAttValue(nameIdentifier[0], nameIdentifier[1], false,
								true).getText().toString());
				String address = getAddress((TagNode) tagNode);
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
