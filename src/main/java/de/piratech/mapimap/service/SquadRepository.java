/**
 *
 */
package de.piratech.mapimap.service;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;

import de.piratech.mapimap.data.Squad;

/**
 * @author maria
 * 
 */
public class SquadRepository extends CouchDbRepositorySupport<Squad> {

	protected SquadRepository(Class<Squad> _type, CouchDbConnector _db) {
		super(_type, _db);
	}

	public List<Squad> getSquads() {
		return queryView("squads/all");
	}

	public boolean squadExists(final Squad _newCrew) {
		List<Squad> existingCrews = findByWikiUrl(_newCrew.getWikiUrl());
		if (!existingCrews.isEmpty()) {
			_newCrew.setId(existingCrews.get(0).getId());
			_newCrew.setRevision(existingCrews.get(0).getRevision());
			return true;
		}
		return false;
	}

	@GenerateView
	public List<Squad> findByWikiUrl(final String _url) {
		return queryView("all", _url);
	}
}
