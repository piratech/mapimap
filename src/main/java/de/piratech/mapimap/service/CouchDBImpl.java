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
import de.piratech.mapimap.data.Squad;
import org.apache.commons.lang3.StringUtils;

/**
 * @author maria
 * 
 */
public class CouchDBImpl implements DataSource {

	private static final Logger LOG = LoggerFactory.getLogger(CouchDBImpl.class);
	private CrewsRepository crewRepo;
	private SquadRepository squadRepo;

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
		Crew crewInDB = crewRepo.findByWikiUrl(_crew.getWikiUrl()).get(0);
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
		throw new UnsupportedOperationException("not implemented yet");
	}

	public void updateSquad(final Squad _crew) {
		// Update crew only if something has changed
		Squad crewInDB = squadRepo.findByWikiUrl(_crew.getWikiUrl()).get(0);
		if (StringUtils.equals(crewInDB.getCheckSum(), _crew.getCheckSum())) {
			LOG.info("updating crew  {}", _crew.getName());
			squadRepo.update(_crew);
		}
	}
}
