/**
 * 
 */
package de.piratech.mapimap.service;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;

import de.piratech.mapimap.data.Crew;

/**
 * @author maria
 * 
 */
public class CrewsRepository extends CouchDbRepositorySupport<Crew> {

	protected CrewsRepository(Class<Crew> type, CouchDbConnector db) {
		super(type, db);
	}

	public List<Crew> getCrews() {
		return queryView("crews/all");
	}

	public boolean crewExists(Crew newCrew) {
		List<Crew> existingCrews = findByWikiUrl(newCrew.getWikiUrl());
		if (existingCrews.size() > 0) {
			newCrew.setId(existingCrews.get(0).getId());
			newCrew.setRevision(existingCrews.get(0).getRevision());
			return true;
		}
		return false;
	}

	@GenerateView
	public List<Crew> findByWikiUrl(String url) {
		return queryView("all", url);
	}
}
