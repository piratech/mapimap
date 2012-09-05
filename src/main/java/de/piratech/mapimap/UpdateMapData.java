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

import de.piratech.mapimap.data.Meeting;
import de.piratech.mapimap.data.Stammtisch;
import de.piratech.mapimap.data.source.Source;
import de.piratech.mapimap.service.CouchDBImpl;
import de.piratech.mapimap.service.DataSource;
import de.piratech.mapimap.service.Geocoder;
import de.piratech.mapimap.service.NominatimGeocoderImpl;
import de.piratech.mapimap.service.meetingcollector.MeetingCollector;
import de.piratech.mapimap.service.meetingcollector.MeetingCollectorFactory;
import de.piratech.mapimap.service.meetingcollector.html.AttributeMatcher;
import de.piratech.mapimap.service.meetingcollector.html.HTMLSource;

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

		// BW
		Map<String, Object> informationIdentifier2 = new HashMap<String, Object>();
		informationIdentifier2.put(HTMLSource.NAME_TAG, 4);
		informationIdentifier2.put(HTMLSource.STREET_TAG, 2);
		informationIdentifier2.put(HTMLSource.ZIP_TAG, 3);
		informationIdentifier2.put(HTMLSource.TOWN_TAG, 4);
		informationIdentifier2.put(HTMLSource.URL_TAG, 0);
		informationIdentifier2.put(HTMLSource.LON_TAG, 6);
		informationIdentifier2.put(HTMLSource.LAT_TAG, 5);
		informationIdentifier2.put(HTMLSource.MEETING_TAG, new AttributeMatcher(
				"class", "sortable"));

		Geocoder geocoder = new NominatimGeocoderImpl();
		DataSource db = createDataSource(properties);
		List<Source> sources = db.getSources();

		for (Source source : sources) {
			MeetingCollector collector = MeetingCollectorFactory.getInstance(source,
					geocoder);
			List<Meeting> meetings = collector.getMeetings();
			LOG.info("found {} {}, try to add them to database...", meetings.size(),
					source.getName());
			if (!meetings.isEmpty()) {
				DataSource dataSource = createDataSource(properties);
				for (Meeting stammtisch : meetings) {
					dataSource.addMeeting(stammtisch);
				}
			}
		}

		// MeetingCollector collector2 = new HTMLTableMeetingCollector(htmlsource2,
		// geocoder, new MeetingFactory<Stammtisch>(Stammtisch.class));
		// List<Meeting> bwStammtische = collector2.getMeetings();
		// LOG.info("found {} bw Stammmtische, try to add them to database...",
		// bwStammtische.size());
		// if (!bwStammtische.isEmpty()) {
		// DataSource dataSource = createDataSource(properties);
		// for (Meeting stammtisch : bwStammtische) {
		// stammtisch.setWikiUrl("http://wiki.piratenpartei.de"
		// + stammtisch.getWikiUrl());
		// dataSource.addStammtisch((Stammtisch) stammtisch);
		// }
		// }

	}

	private static Class<Meeting> getMeetingClass(Source source) {
		try {
			return (Class<Meeting>) (Class.forName(source.getMeetingType()
					.getClassName()));
		} catch (ClassNotFoundException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
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
