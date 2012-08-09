package de.piratech.mapimap.service;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;

import de.piratech.mapimap.data.Stammtisch;

public class StammtischRepository extends CouchDbRepositorySupport<Stammtisch> {

	protected StammtischRepository(Class<Stammtisch> _type, CouchDbConnector _db) {
		super(_type, _db);
	}

	public boolean stammtischExists(final Stammtisch _newCrew) {
		List<Stammtisch> existingCrews = findByWikiUrl(_newCrew.getWikiUrl());
		if (!existingCrews.isEmpty()) {
			_newCrew.setId(existingCrews.get(0).getId());
			_newCrew.setRevision(existingCrews.get(0).getRevision());
			return true;
		}
		return false;
	}

	@GenerateView
	public List<Stammtisch> findByWikiUrl(final String _url) {
		return queryView("all", _url);
	}

}
