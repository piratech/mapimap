package de.piratech.mapimap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.piratech.mapimap.data.Crew;
import de.piratech.mapimap.data.Meeting;
import de.piratech.mapimap.data.MeetingFactory;
import de.piratech.mapimap.data.Squad;
import de.piratech.mapimap.data.Stammtisch;
import de.piratech.mapimap.service.CouchDBImpl;
import de.piratech.mapimap.service.DataSource;
import de.piratech.mapimap.service.Geocoder;
import de.piratech.mapimap.service.NominatimGeocoderImpl;
import de.piratech.mapimap.service.meetingcollector.MeetingCollector;
import de.piratech.mapimap.service.meetingcollector.html.AttributeMatcher;
import de.piratech.mapimap.service.meetingcollector.html.HTMLMeetingCollectorLinkList;
import de.piratech.mapimap.service.meetingcollector.html.HTMLSource;
import de.piratech.mapimap.service.meetingcollector.html.HTMLTableMeetingCollector;
import de.piratech.mapimap.service.meetingcollector.html.HTMLAttributeMeetingCollector;

/**
 * @author maria
 * 
 */
public class UpdateMapData {

	// @todo: deveth0@geirkairam: there are many potential NullPointerExceptions
	// @todo: deveth0@geirkairam: probably there should be a command
	// "check config" which connects to the database to test the settings
	private static final Logger LOG = LoggerFactory
			.getLogger(UpdateMapData.class);

	/**
	 * @param _args
	 */
	public static void main(final String[] _args) {
		if (_args.length == 0 || (StringUtils.equals(_args[0], "help"))) {
			System.out.println("Usage:");
			System.out.println("java -jar yourfile.jar COMMAND your.properties");
			System.out.println("COMMANDS:");
			System.out.println("updateDB: updates the DB");
			System.out.println("deleteAll: deletes content");
			return;
		}
		try {
			String task = _args[0];
			if (task.equals("updateDB")) {
				updateDB(_args[1]);
			} else if (task.equals("deleteAll")) {
				deleteAll(_args[1]);
			} else {
				LOG.error("task >{}< not supported", _args[0]);
			}
		} catch (Exception e) {
			LOG.error("message is {}", e);
		}
	}

	private static void deleteAll(String _propertiesURI)
			throws FileNotFoundException, IOException {
		LOG.info("perform task delete...");
		Properties properties = loadProperties(_propertiesURI);
		DataSource dataSource = createDataSource(properties);
		for (Stammtisch crew : dataSource.getStammtische()) {
			LOG.info("delete crew {}", crew.getName());
			dataSource.delete(crew);
		}
	}

	private static void updateDB(final String _propertiesURI)
			throws FileNotFoundException, IOException {
		LOG.info("perform task update...");
		Properties properties = loadProperties(_propertiesURI);

		// TODO put all that meeting specific stuff in property filee, maybe json
		// format or xml... vielleicht HTMLSource object irgendwie parsen...
		Map<String, Object> informationKeks = new HashMap<String, Object>();
		informationKeks.put(HTMLSource.NAME_TAG, new AttributeMatcher("class",
				"name"));
		informationKeks.put(HTMLSource.MEETING_TAG, new AttributeMatcher("title",
				"BE:Squads", AttributeMatcher.STARTS_WITH));
		informationKeks.put(HTMLSource.ADDRESS_TAG, new AttributeMatcher("class",
				"address"));
		HTMLSource squadSource = new HTMLSource(informationKeks,
				"http://wiki.piratenpartei.de/Vorlage:Berlin_Navigationsleiste_Squads");

		Map<String, Object> informationKeks2 = new HashMap<String, Object>();
		informationKeks2.put(HTMLSource.NAME_TAG, new AttributeMatcher("class",
				"name"));
		informationKeks2.put(HTMLSource.MEETING_TAG, new AttributeMatcher("class",
				"crewBerlin"));
		informationKeks2.put(HTMLSource.ADDRESS_TAG, new AttributeMatcher("class",
				"address"));

		HTMLSource crewSource = new HTMLSource(informationKeks2,
				"http://wiki.piratenpartei.de/BE:Crews/Crewmap");

		Map<String, Object> informationIdentifier = new HashMap<String, Object>();
		informationIdentifier.put(HTMLSource.NAME_TAG, 1);
		informationIdentifier.put(HTMLSource.STREET_TAG, 3);
		informationIdentifier.put(HTMLSource.ZIP_TAG, 4);
		informationIdentifier.put(HTMLSource.TOWN_TAG, 5);
		informationIdentifier.put(HTMLSource.URL_TAG, 0);
		informationIdentifier.put(HTMLSource.LON_TAG, 7);
		informationIdentifier.put(HTMLSource.LAT_TAG, 8);
		informationIdentifier.put(HTMLSource.MEETING_TAG, new AttributeMatcher(
				"class", "smwtable"));

		HTMLSource htmlsource = new HTMLSource(informationIdentifier,
				"http://wiki.piratenpartei.de/Vorlage:HE:Piratentreff/TabelleLandDaten");
		htmlsource.setTable(true);

		Geocoder geocoder = new NominatimGeocoderImpl();
		MeetingCollector berlinCrewsSource = new HTMLAttributeMeetingCollector(
				crewSource, geocoder, new MeetingFactory<Crew>(Crew.class));
		List<Meeting> crews = berlinCrewsSource.getMeetings();
		LOG.info("found {} crews, try to add them to database...", crews.size());
		if (!crews.isEmpty()) {
			DataSource dataSource = createDataSource(properties);
			for (Meeting crew : crews) {
				crew.setWikiUrl("http://wiki.piratenpartei.de/BE:Crews/"
						+ wikiURLEncode(crew.getName()));
				dataSource.addCrew((Crew) crew);
			}
		}

		MeetingCollector collector = new HTMLTableMeetingCollector(htmlsource,
				geocoder, new MeetingFactory<Stammtisch>(Stammtisch.class));
		List<Meeting> hessenStammtische = collector.getMeetings();
		LOG.info("found {} hessische Stammmtische, try to add them to database...",
				hessenStammtische.size());
		if (!hessenStammtische.isEmpty()) {
			DataSource dataSource = createDataSource(properties);
			for (Meeting stammtisch : hessenStammtische) {
				stammtisch.setWikiUrl("http://wiki.piratenpartei.de"
						+ stammtisch.getWikiUrl());
				dataSource.addStammtisch((Stammtisch) stammtisch);
			}
		}

	}

	private static String wikiURLEncode(String urlPeace)
			throws UnsupportedEncodingException {
		return URLEncoder.encode(urlPeace.replaceAll(" ", "_"), "UTF-8");
	}

	private static DataSource createDataSource(final Properties _properties) {
		DataSource dataSource = new CouchDBImpl(
				_properties.getProperty("parser.couchInstance"),
				_properties.getProperty("parser.couchInstanceUser"),
				_properties.getProperty("parser.couchInstancePassword"),
				_properties.getProperty("parser.couchInstanceDB"));
		return dataSource;
	}

	private static Properties loadProperties(final String _propertiesURI)
			throws FileNotFoundException, IOException {
		if (StringUtils.isBlank(_propertiesURI)) {
			LOG.error("no properties defined");
			return null;
		}
		// return new Properties().load(new FileInputStream(propertiesURI));
		Properties properties = new Properties();
		properties.load(new FileInputStream(_propertiesURI));
		return properties;
	}
}
