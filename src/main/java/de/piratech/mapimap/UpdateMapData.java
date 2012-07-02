package de.piratech.mapimap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.piratech.mapimap.data.Crew;
import de.piratech.mapimap.service.BerlinCrews;
import de.piratech.mapimap.service.BerlinCrewsImpl;
import de.piratech.mapimap.service.CouchDBImpl;
import de.piratech.mapimap.service.DataSource;
import de.piratech.mapimap.service.GeocoderImpl;
import de.piratech.mapimap.service.Geocoder;

/**
 * @author maria
 * 
 */
public class UpdateMapData {

	private static final Logger LOG = LoggerFactory
			.getLogger(UpdateMapData.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LOG.trace("main() enter");
		if (args.length == 0) {
			LOG.error("no arguments :(");
			return;
		}
		try {
			String task = args[0];
			if (task.equals("updateDB")) {
				updateDB(args[1]);
			} else if (task.equals("deleteAll")) {
				deleteAll(args[1]);
			} else {
				LOG.error("task >{}< not supported", args[0]);
			}
		} catch (Exception e) {
			LOG.error("message is {}", e.getMessage());
			e.printStackTrace();
		}
		LOG.trace("main() leave");
	}

	private static void deleteAll(String propertiesURI)
			throws FileNotFoundException, IOException {
		LOG.info("perform task delete...");
		Properties properties = loadProperties(propertiesURI);
		DataSource dataSource = createDataSource(properties);
		List<Crew> crews = dataSource.getCrews();
		for (Crew crew : crews) {
			LOG.info("delete crew {}", crew.getName());
			dataSource.delete(crew);
		}
	}

	private static void updateDB(String propertiesURI)
			throws FileNotFoundException, IOException {
		LOG.info("perform task update...");
		Properties properties = loadProperties(propertiesURI);

		Geocoder geocoder = new GeocoderImpl();
		BerlinCrews berlinCrewsSource = new BerlinCrewsImpl(geocoder);
		List<Crew> crews = berlinCrewsSource.getCrews();

		LOG.info("found {} crews, try to add them to database...", crews.size());

		if (crews.size() > 0) {
			DataSource dataSource = createDataSource(properties);
			for (Crew crew : crews) {
				dataSource.addCrew(crew);
			}
		}

	}

	private static DataSource createDataSource(Properties properties) {
		DataSource dataSource = new CouchDBImpl(
				properties.getProperty("parser.couchInstance"),
				properties.getProperty("parser.couchInstanceUser"),
				properties.getProperty("parser.couchInstancePassword"),
				properties.getProperty("parser.couchInstanceDB"));
		return dataSource;
	}

	private static Properties loadProperties(String propertiesURI)
			throws FileNotFoundException, IOException {
		if (propertiesURI == null || propertiesURI.equals("")) {
			LOG.error("no properties defined");
			return null;
		}
		Properties properties = new Properties();
		properties.load(new FileInputStream(propertiesURI));
		return properties;

	}
}
