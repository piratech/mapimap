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

	public boolean stammtischExists(final Stammtisch stammtisch) {
		List<Stammtisch> existingCrews = findByForeignKey(stammtisch.getForeignKey());
		if (!existingCrews.isEmpty()) {
			stammtisch.setId(existingCrews.get(0).getId());
			stammtisch.setRevision(existingCrews.get(0).getRevision());
			return true;
		}
		return false;
	}

	@GenerateView
	public List<Stammtisch> findByForeignKey(final String foreignKey) {
		return queryView("all", foreignKey);
	}

	public List<Stammtisch> getBySourceId(String sourceId) {
		return queryView("bySourceId", sourceId);
	}

}
