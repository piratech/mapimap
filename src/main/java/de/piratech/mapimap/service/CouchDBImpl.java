/**
 *
 */
package de.piratech.mapimap.service;

import java.net.MalformedURLException;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.piratech.mapimap.data.Crew;
import de.piratech.mapimap.data.Meeting;
import de.piratech.mapimap.data.Squad;
import de.piratech.mapimap.data.Stammtisch;
import de.piratech.mapimap.data.source.Source;

import org.apache.commons.lang3.StringUtils;

/**
 * @author maria
 * 
 */
public class CouchDBImpl implements DataSource {

	private static final Logger LOG = LoggerFactory.getLogger(CouchDBImpl.class);
	private CrewsRepository crewRepo;
	private SquadRepository squadRepo;
	private StammtischRepository stammtischRepo;
	private SourceRepository sourceRepo;

	public CouchDBImpl(final String _url, final String _userName,
			final String _password, final String _database) {
		LOG.debug("DataSourceImpl(url >{}<, userName >{}<, password >{}<)",
				new Object[] { _url, _userName, _password });
		try {
			HttpClient authenticatedHttpClient = new StdHttpClient.Builder()
					.url(_url).username(_userName).password(_password).build();
			StdCouchDbInstance couchDbInstance = new StdCouchDbInstance(
					authenticatedHttpClient);
			CouchDbConnector couchDbConnector = couchDbInstance.createConnector(
					_database, false);
			crewRepo = new CrewsRepository(Crew.class, couchDbConnector);
			squadRepo = new SquadRepository(Squad.class, couchDbConnector);
			stammtischRepo = new StammtischRepository(Stammtisch.class,
					couchDbConnector);
			sourceRepo = new SourceRepository(Source.class, couchDbConnector);
		} catch (MalformedURLException e) {
			LOG.debug("DataSourceImpl(url >{}<, userName >{}<, password >{}<)");
			LOG.error("{}", e);
		}
	}

	@Override
	public List<Crew> getCrews() {
		return crewRepo.getAll();
	}

	@Override
	public void addCrew(final Crew _newCrew) {
		if (!crewRepo.crewExists(_newCrew)) {
			LOG.info("adding crew {} to database", _newCrew.getName());
			crewRepo.add(_newCrew);
		} else {
			updateCrew(_newCrew);
		}
	}

	public void updateCrew(final Crew _crew) {
		// Update crew only if something has changed
		Crew crewInDB = crewRepo.findForeignKey(_crew.getWikiUrl()).get(0);
		if (!StringUtils.equals(crewInDB.getCheckSum(), _crew.getCheckSum())) {
			LOG.info("updating crew  {}", _crew.getName());
			crewRepo.update(_crew);
		}
	}

	@Override
	public void delete(final Crew _crew) {
		crewRepo.remove(_crew);
	}

	@Override
	public List<Squad> getSquads() {
		return squadRepo.getAll();
	}

	@Override
	public void addSquad(Squad _newSquad) {
		if (!squadRepo.squadExists(_newSquad)) {
			LOG.info("adding crew {} to database", _newSquad.getName());
			squadRepo.add(_newSquad);
		} else {
			updateSquad(_newSquad);
		}
	}

	@Override
	public void delete(Squad _squad) {
		squadRepo.remove(_squad);
	}

	public void updateSquad(final Squad _crew) {
		// Update crew only if something has changed
		Squad crewInDB = squadRepo.findForeignKey(_crew.getWikiUrl()).get(0);
		if (!StringUtils.equals(crewInDB.getCheckSum(), _crew.getCheckSum())) {
			LOG.info("updating crew  {}", _crew.getName());
			squadRepo.update(_crew);
		}
	}

	@Override
	public List<Stammtisch> getStammtische() {
		return stammtischRepo.getAll();
	}

	@Override
	public void delete(Stammtisch stammtich) {
		stammtischRepo.remove(stammtich);
	}

	@Override
	public void addStammtisch(Stammtisch _newSquad) {
		if (!stammtischRepo.stammtischExists(_newSquad)) {
			LOG.info("adding stammtisch {} to database", _newSquad.getName());
			stammtischRepo.add(_newSquad);
		} else {
			updateStammtich(_newSquad);
		}
	}

	public void updateStammtich(final Stammtisch _crew) {
		// Update crew only if something has changed
		Stammtisch crewInDB = stammtischRepo.findByForeignKey(_crew.getWikiUrl()).get(
				0);
		if (!StringUtils.equals(crewInDB.getCheckSum(), _crew.getCheckSum())) {
			LOG.info("updating stammtisch  {}", _crew.getName());
			stammtischRepo.update(_crew);
		}
	}

	@Override
	public void addSource(Source source) {
		if (!sourceRepo.sourceExists(source)) {
			sourceRepo.add(source);
		}
	}

	@Override
	public List<Source> getSources() {
		return sourceRepo.getAll();
	}

	public void updateSource(final Source _crew) {
		// Update crew only if something has changed
		Source crewInDB = sourceRepo.findByUrl(_crew.getUrl()).get(0);
		if (!StringUtils.equals(crewInDB.getCheckSum(), _crew.getCheckSum())) {
			LOG.info("updating crew  {}", _crew.getName());
			sourceRepo.update(_crew);
		}
	}

	@Override
	public void delete(Source stammtich) {
		sourceRepo.remove(stammtich);
	}

	@Override
	public void addMeeting(Meeting meeting) {
		if (meeting instanceof Stammtisch) {
			addStammtisch((Stammtisch) meeting);
		} else if (meeting instanceof Crew) {
			addCrew((Crew) meeting);
		} else if (meeting instanceof Squad) {
			addSquad((Squad) meeting);
		} else {
			LOG.error("meeting type {} not handled", meeting.getClass());
		}
	}

}
