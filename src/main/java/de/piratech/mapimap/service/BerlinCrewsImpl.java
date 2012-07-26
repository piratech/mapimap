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

/**
 * @author maria
 * 
 */
public class BerlinCrewsImpl implements BerlinCrews {

	private static final String wikiURL = "http://wiki.piratenpartei.de/BE:Crews/Crewmap";
	private static final Logger LOG = LoggerFactory
			.getLogger(BerlinCrewsImpl.class);
	private Geocoder geocoder;
	private HttpClient client;

	public BerlinCrewsImpl(final Geocoder _geocoder) {
		this.geocoder = _geocoder;
		this.client = new DefaultHttpClient();
	}

	@Override
	public List<Crew> getCrews() {
		HttpGet get = new HttpGet(wikiURL);
		try {

			HttpResponse response = client.execute(get);

			// very old look for something never (cannot use org.w3c.dom.Document
			// because wiki sends invalid HTML)
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode node = cleaner.clean(response.getEntity().getContent());
			List<?> elementListByAttValue = node.getElementListByAttValue("class",
					"crewBerlin", true, true);
			List<Crew> crews = new ArrayList<Crew>();
			for (Object tagNode : elementListByAttValue) {
				Crew crew = new Crew();
				crew.setName(((TagNode) tagNode)
						.findElementByAttValue("class", "name", false, true).getText()
						.toString());
				// @todo: deveth0@geirkairam: should be configurable (eg.
				// settings.properties)
				crew.setWikiUrl("http://wiki.piratenpartei.de/BE:Crews/"
						+ crew.getName());
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
		TagNode addressTag = _tagNode.findElementByAttValue("class", "address",
				true, true);
		if (addressTag != null) {
			address = addressTag.getText().toString();
		}
		return address;
	}
}
