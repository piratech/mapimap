package de.piratech.mapimap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.piratech.mapimap.data.Crew;
import de.piratech.mapimap.data.Meeting;
import de.piratech.mapimap.data.Squad;
import de.piratech.mapimap.data.Stammtisch;
import de.piratech.mapimap.data.source.Source;
import de.piratech.mapimap.service.CouchDBImpl;
import de.piratech.mapimap.service.DataSource;
import de.piratech.mapimap.service.Geocoder;
import de.piratech.mapimap.service.NominatimGeocoderImpl;
import de.piratech.mapimap.service.meetingcollector.MeetingCollector;
import de.piratech.mapimap.service.meetingcollector.MeetingCollectorFactory;

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
			System.out
					.println("java -jar yourfile.jar COMMAND your.properties");
			System.out.println("COMMANDS:");
			System.out.println("updateDB: updates the DB");
			System.out.println("deleteAll: deletes content");
			System.out
					.println("updateFromSource: updates the source defined by id in property file");
			return;
		}
		try {
			String task = _args[0];
			if (task.equals("updateDB")) {
				updateDB(_args[1]);
			} else if (task.equals("deleteAll")) {
				deleteAll(_args[1]);
			} else if (task.equals("updateFromSource")) {
				updateFromSource(_args[1]);
			} else {
				LOG.error("task >{}< not supported", _args[0]);
			}
		} catch (Exception e) {
			LOG.error("message is {}", e);
		}
	}

	private static void updateFromSource(String _propertiesURI)
			throws FileNotFoundException, IOException {
		LOG.info("perform task update source...");
		Properties properties = loadProperties(_propertiesURI);

		Geocoder geocoder = new NominatimGeocoderImpl();
		DataSource db = createDataSource(properties);
		List<Source> sources = db.getSources();
		LOG.info("found {} sources", sources.size());
		for (Source source : sources) {
			if (source.getId().equals(properties.get("update.this"))) {
				LOG.info("start scraping for {} from {}", source.getName(),
						source.getBase() + source.getUrl());
				MeetingCollector collector = MeetingCollectorFactory
						.getInstance(source, geocoder);
				List<Meeting> meetings = collector.getMeetings();
				LOG.info("found {} {}, try to add them to database...",
						meetings.size(), source.getName());
				if (!meetings.isEmpty()) {
					DataSource dataSource = createDataSource(properties);
					for (Meeting stammtisch : meetings) {
						dataSource.addMeeting(stammtisch);
					}
				}
			}
		}
		LOG.info("... update finished");
	}

	private static void deleteAll(String _propertiesURI)
			throws FileNotFoundException, IOException {
		LOG.info("perform task delete...");
		Properties properties = loadProperties(_propertiesURI);
		DataSource dataSource = createDataSource(properties);
		for (Stammtisch crew : dataSource.getStammtische()) {
			LOG.info("delete {}", crew.getName());
			dataSource.delete(crew);
		}
		for (Crew crew : dataSource.getCrews()) {
			LOG.info("delete {}", crew.getName());
			dataSource.delete(crew);
		}
		for (Squad crew : dataSource.getSquads()) {
			LOG.info("delete {}", crew.getName());
			dataSource.delete(crew);
		}
	}

	private static void updateDB(final String _propertiesURI)
			throws FileNotFoundException, IOException {
		LOG.info("perform task update...");
		Properties properties = loadProperties(_propertiesURI);

		Geocoder geocoder = new NominatimGeocoderImpl();
		DataSource db = createDataSource(properties);
		List<Source> sources = db.getSources();
		LOG.info("found {} sources", sources.size());
		for (Source source : sources) {
			LOG.info("start scraping for {} from {}", source.getName(),
					source.getBase() + source.getUrl());
			MeetingCollector collector = MeetingCollectorFactory.getInstance(
					source, geocoder);
			List<Meeting> meetings = collector.getMeetings();
			LOG.info("found {} {}, try to add them to database...",
					meetings.size(), source.getName());
			if (!meetings.isEmpty()) {
				DataSource dataSource = createDataSource(properties);
				for (Meeting stammtisch : meetings) {
					dataSource.addMeeting(stammtisch);
				}
			}
		}
		LOG.info("... update finished");
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
		Properties properties = new Properties();
		properties.load(new FileInputStream(_propertiesURI));
		return properties;
	}
}
