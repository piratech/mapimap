package de.piratech.mapimap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.piratech.mapimap.data.Crew;
import de.piratech.mapimap.data.Meeting;
import de.piratech.mapimap.data.MeetingFactory;
import de.piratech.mapimap.data.Squad;
import de.piratech.mapimap.service.CouchDBImpl;
import de.piratech.mapimap.service.DataSource;
import de.piratech.mapimap.service.Geocoder;
import de.piratech.mapimap.service.NominatimGeocoderImpl;
import de.piratech.mapimap.service.meetingcollector.MeetingCollector;
import de.piratech.mapimap.service.meetingcollector.html.AttributeMatcher;
import de.piratech.mapimap.service.meetingcollector.html.HTMLMeetingCollectorLinkList;
import de.piratech.mapimap.service.meetingcollector.html.HTMLMeetingCollectorOneSite;
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
		for (Crew crew : dataSource.getCrews()) {
			LOG.info("delete crew {}", crew.getName());
			dataSource.delete(crew);
		}
	}

	private static void updateDB(final String _propertiesURI)
			throws FileNotFoundException, IOException {
		LOG.info("perform task update...");
		Properties properties = loadProperties(_propertiesURI);

		HTMLSource squadSource = new HTMLSource(new AttributeMatcher("title",
				"BE:Squads", AttributeMatcher.STARTS_WITH), new AttributeMatcher(
				"class", "name"), new AttributeMatcher("class", "address"),
				"http://wiki.piratenpartei.de/Vorlage:Berlin_Navigationsleiste_Squads");

		HTMLSource crewSource = new HTMLSource(new AttributeMatcher("class",
				"crewBerlin"), new AttributeMatcher("class", "name"),
				new AttributeMatcher("class", "address"),
				"http://wiki.piratenpartei.de/BE:Crews/Crewmap");

		Geocoder geocoder = new NominatimGeocoderImpl();
		MeetingCollector berlinCrewsSource = new HTMLMeetingCollectorOneSite<Crew>(
				crewSource, geocoder, new MeetingFactory<Crew>(Crew.class));
		List<Meeting> crews = berlinCrewsSource.getMeetings();
		LOG.info("found {} crews, try to add them to database...", crews.size());
		if (!crews.isEmpty()) {
			DataSource dataSource = createDataSource(properties);
			for (Meeting crew : crews) {
				crew.setWikiUrl("http://wiki.piratenpartei.de/BE:Crews/"
						+ URLEncoder.encode(crew.getName().replaceAll(" ", "_"), "UTF-8"));
				dataSource.addCrew((Crew) crew);
			}
		}

		MeetingCollector berlinSquadsSource = new HTMLMeetingCollectorLinkList<Squad>(
				squadSource, geocoder, "http://wiki.piratenpartei.de",
				new MeetingFactory<Squad>(Squad.class));
		List<Meeting> crews2 = berlinSquadsSource.getMeetings();
		LOG.info("found {} squads, try to add them to database...", crews2.size());
		if (!crews2.isEmpty()) {
			DataSource dataSource = createDataSource(properties);
			for (Meeting crew : crews2) {
				dataSource.addSquad((Squad) crew);
			}
		}

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
