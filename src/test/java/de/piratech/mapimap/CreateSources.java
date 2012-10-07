/**
 * 
 */
package de.piratech.mapimap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.piratech.mapimap.data.MeetingType;
import de.piratech.mapimap.data.source.Source;
import de.piratech.mapimap.data.source.SourceInformationIdentifier;
import de.piratech.mapimap.data.source.SourceInformationIdentifierType;
import de.piratech.mapimap.data.source.SourceType;
import de.piratech.mapimap.service.CouchDBImpl;
import de.piratech.mapimap.service.DataSource;
import de.piratech.mapimap.service.meetingcollector.html.AttributeMatcher;

/**
 * @author maria
 * 
 */
public class CreateSources {

	private static final Logger LOG = LoggerFactory
			.getLogger(CreateSources.class);

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		Source berlinCrews = createBerlinCrewsSource();
		Source berlinSquads = createBerlinSquadSource();
		Source hessen = createHessenStammtischSource();
		Source bayern = createBayernStammtischSource();
		Source niedersachsen = createNiedersachenStammtischSource();
		Source badenwuertemberg = createBadenWuertembergStammtischSource();
		Source nrw = createNRWStammtischSource();

		Properties properties = loadProperties(args[0]);
		DataSource dataSource = createDataSource(properties);
		List<Source> sources = dataSource.getSources();
		for (Source source : sources) {
			dataSource.delete(source);
		}

		dataSource.addSource(hessen);
		dataSource.addSource(bayern);
		dataSource.addSource(niedersachsen);
		dataSource.addSource(berlinCrews);
		dataSource.addSource(berlinSquads);
		dataSource.addSource(badenwuertemberg);
		dataSource.addSource(nrw);
	}

	private static Source createNRWStammtischSource() {
		Source source = createHTMLTableSource("http://wiki.piratenpartei.de","/Benutzer:Geirkairam/temp", -1, 1, 2, 3, 0, -1,
				-1, "smwtable");
		source.setMeetingType(MeetingType.STAMMTISCH);
		source.setName("NRW Stammtische");
		source.setMeetingType(MeetingType.STAMMTISCH);
		source.setSourceType(SourceType.HTMLTABLE);
		
		return source;
	}

	private static Source createBadenWuertembergStammtischSource() {
		Source source = new Source();
		source.setMeetingType(MeetingType.STAMMTISCH);
		source.setUrl("http://api.piraten-bw.de/stammtisch/webcal/");
		source.setName("baden-würtembergische Stammtische");
		source.setMeetingType(MeetingType.STAMMTISCH);
		source.setSourceType(SourceType.ICAL);
		source.setDeleteBeforeUpdate(true);
		return source;
	}

	private static Source createNiedersachenStammtischSource() {
		Source niedersachsen = createHTMLTableSource(
				"http://wiki.piratenpartei.de", "/NSD:Stammtische/MapData", 4,
				2, 3, 4, 0, 6, 5, "sortable");
		niedersachsen.setName("niedersächsische Stammtische");
		niedersachsen.setMeetingType(MeetingType.STAMMTISCH);
		niedersachsen.setSourceType(SourceType.HTMLTABLE);
		return niedersachsen;
	}

	private static Source createBayernStammtischSource() {
		Source bayern = createHTMLTableSource("http://wiki.piratenpartei.de",
				"/BY:Stammtische/MapData", 4, 2, 3, 4, 0, 6, 5, "sortable");
		bayern.setName("bayrische Stammtische");
		bayern.setMeetingType(MeetingType.STAMMTISCH);
		bayern.setSourceType(SourceType.HTMLTABLE);
		return bayern;
	}

	private static Source createHessenStammtischSource() {
		Source hessen = createHTMLTableSource("http://wiki.piratenpartei.de",
				"/Vorlage:HE:Piratentreff/TabelleLandDaten", 1, 3, 4, 5, 0, 7,
				8, "smwtable");
		hessen.setName("hessiche Stammtische");
		hessen.setMeetingType(MeetingType.STAMMTISCH);
		hessen.setSourceType(SourceType.HTMLTABLE);
		return hessen;
	}

	private static Source createBerlinCrewsSource() {
		Source berlinCrews = createHTMLAttributePageSource(
				"http://wiki.piratenpartei.de", "/BE:Crews/Crewmap", "name",
				"", "", "", "address", "url", "lon", "lat", "crewBerlin",
				AttributeMatcher.EXACT_MATCH);
		berlinCrews.setName("berliner Crews");
		berlinCrews.setMeetingType(MeetingType.CREW);
		berlinCrews.setSourceType(SourceType.HTMLATTRIBUTEPAGE);
		return berlinCrews;
	}

	private static Source createBerlinSquadSource() {
		Source berlinSquads = new Source();
		berlinSquads.setSourceType(SourceType.HTMLATTRIBUTEPAGE);
		berlinSquads.setUrl("/Vorlage:Berlin_Navigationsleiste_Squads");
		berlinSquads.setBase("http://wiki.piratenpartei.de");
		berlinSquads.setInformationTypes(Arrays
				.asList(new SourceInformationIdentifier<?>[] {
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.NAME,
								"attribute", new AttributeMatcher("class",
										"name", AttributeMatcher.EXACT_MATCH)),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.STREET, "",
								new AttributeMatcher("class", "",
										AttributeMatcher.EXACT_MATCH)),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.ZIP,
								"attribute", new AttributeMatcher("class", "",
										AttributeMatcher.EXACT_MATCH)),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.TOWN,
								"attribute", new AttributeMatcher("class", "",
										AttributeMatcher.EXACT_MATCH)),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.URL,
								"attribute", new AttributeMatcher("class", "",
										AttributeMatcher.EXACT_MATCH)),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.LON,
								"attribute", new AttributeMatcher("class", "",
										AttributeMatcher.EXACT_MATCH)),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.ADDRESS,
								"attribute",
								new AttributeMatcher("class", "address",
										AttributeMatcher.EXACT_MATCH)),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.LAT,
								"attribute", new AttributeMatcher("class", "",
										AttributeMatcher.EXACT_MATCH)),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.MEETING,
								"attribute", new AttributeMatcher("title",
										"BE:Squads",
										AttributeMatcher.STARTS_WITH)) }));
		berlinSquads.setName("berliner Squads");
		berlinSquads.setMeetingType(MeetingType.SQUAD);
		berlinSquads.setSourceType(SourceType.HTMLLINKLIST);
		return berlinSquads;
	}

	private static Source createHTMLAttributePageSource(String base,
			String url, String nameClass, String steetClass, String zipClass,
			String townClass, String addressClass, String urlClass,
			String lonClass, String latClass, String meetingClass,
			int meetingMatchMode) {
		Source source = new Source();
		source.setSourceType(SourceType.HTMLATTRIBUTEPAGE);
		source.setUrl(url);
		source.setBase(base);
		source.setDeleteBeforeUpdate(false);
		source.setInformationTypes(Arrays
				.asList(new SourceInformationIdentifier<?>[] {
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.NAME,
								"attribute",
								new AttributeMatcher("class", nameClass,
										AttributeMatcher.EXACT_MATCH)),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.STREET,
								"attribute", new AttributeMatcher("class",
										steetClass,
										AttributeMatcher.EXACT_MATCH)),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.ZIP,
								"attribute", new AttributeMatcher("class",
										zipClass, AttributeMatcher.EXACT_MATCH)),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.TOWN,
								"attribute",
								new AttributeMatcher("class", townClass,
										AttributeMatcher.EXACT_MATCH)),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.URL,
								"attribute", new AttributeMatcher("class",
										urlClass, AttributeMatcher.EXACT_MATCH)),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.LON,
								"attribute", new AttributeMatcher("class",
										lonClass, AttributeMatcher.EXACT_MATCH)),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.ADDRESS,
								"attribute", new AttributeMatcher("class",
										addressClass,
										AttributeMatcher.EXACT_MATCH)),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.LAT,
								"attribute", new AttributeMatcher("class",
										latClass, AttributeMatcher.EXACT_MATCH)),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.MEETING,
								"attribute", new AttributeMatcher("class",
										meetingClass, meetingMatchMode)) }));
		return source;
	}

	private static Source createHTMLTableSource(String base, String url,
			Integer nameColumn, Integer streetColumn, Integer zipColumn,
			Integer townColumn, Integer urlColumn, Integer lonColumn,
			Integer latColumn, String tableClass) {
		Source source = new Source();
		source.setSourceType(SourceType.HTMLTABLE);
		source.setUrl(url);
		source.setBase(base);

		source.setInformationTypes(Arrays
				.asList(new SourceInformationIdentifier<?>[] {
						new SourceInformationIdentifier<Integer>(
								SourceInformationIdentifierType.NAME, "column",
								nameColumn),
						new SourceInformationIdentifier<Integer>(
								SourceInformationIdentifierType.STREET,
								"column", streetColumn),
						new SourceInformationIdentifier<Integer>(
								SourceInformationIdentifierType.ZIP, "column",
								zipColumn),
						new SourceInformationIdentifier<Integer>(
								SourceInformationIdentifierType.TOWN, "column",
								townColumn),
						new SourceInformationIdentifier<Integer>(
								SourceInformationIdentifierType.URL, "column",
								urlColumn),
						new SourceInformationIdentifier<Integer>(
								SourceInformationIdentifierType.LON, "column",
								lonColumn),
						new SourceInformationIdentifier<Integer>(
								SourceInformationIdentifierType.LAT, "column",
								latColumn),
						new SourceInformationIdentifier<AttributeMatcher>(
								SourceInformationIdentifierType.MEETING,
								"attribute", new AttributeMatcher("class",
										tableClass,
										AttributeMatcher.EXACT_MATCH)) }));
		return source;
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
