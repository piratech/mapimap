/**
 * 
 */
package de.piratech.mapimap.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	public BerlinCrewsImpl(Geocoder geocoder) {
		LOG.trace("enter with sourceURL >{}< and geocoder {}", wikiURL,
				geocoder);
		this.geocoder = geocoder;
		this.client = new DefaultHttpClient();
	}

	public List<Crew> getCrews() {
		LOG.trace("enter");
		HttpGet get = new HttpGet(wikiURL);
		try {

			HttpResponse response = client.execute(get);

			// very old look for something never (cannot use
			// org.w3c.dom.Document because wiki sends invalid HTML)
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode node = cleaner.clean(response.getEntity().getContent());
			@SuppressWarnings("unchecked")
			List<TagNode> elementListByAttValue = node
					.getElementListByAttValue("class", "crewBerlin", true, true);
			List<Crew> crews = new ArrayList<Crew>();
			for (TagNode tagNode : elementListByAttValue) {
				Crew crew = new Crew();
				crew.setName(tagNode
						.findElementByAttValue("class", "name", false, true)
						.getText().toString());
				crew.setWikiUrl("http://wiki.piratenpartei.de/BE:Crews/"
						+ crew.getName());
				String address = getAddress(tagNode);
				if (address != null && !address.equals("")) {
					LocationData locationData = geocoder
							.getLocationData(address);
					if (locationData != null) {
						crew.setLocationData(locationData);
						crews.add(crew);
					} else {
						LOG.warn(
								"skip crew {} (url: {}) because location not found",
								crew.getName(), crew.getWikiUrl());
					}
				} else {
					LOG.warn(
							"skip crew {} (url: {}) because address not found",
							crew.getName(), crew.getWikiUrl());
				}
			}
			return crews;
		} catch (IOException e) {
			LOG.error("message: {}", e.getMessage());
			e.printStackTrace();
		} catch (IllegalStateException e) {
			LOG.error("message: {}", e.getMessage());
			e.printStackTrace();
		}
		LOG.trace("leave returning null");
		return null;
	}

	private String getAddress(TagNode tagNode) {
		String address = null;
		TagNode addressTag = tagNode.findElementByAttValue("class", "address",
				true, true);
		if (addressTag != null) {
			address = addressTag.getText().toString();
		}
		return address;
	}
}
