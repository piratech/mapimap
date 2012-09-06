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

	protected CrewsRepository(Class<Crew> _type, CouchDbConnector _db) {
		super(_type, _db);
	}

	public List<Crew> getCrews() {
		return queryView("crews/all");
	}

	public boolean crewExists(final Crew _newCrew) {
		List<Crew> existingCrews = findForeignKey(_newCrew.getForeignKey());
		if (!existingCrews.isEmpty()) {
			_newCrew.setId(existingCrews.get(0).getId());
			_newCrew.setRevision(existingCrews.get(0).getRevision());
			return true;
		}
		return false;
	}

	@GenerateView
	public List<Crew> findForeignKey(final String foreignKey) {
		return queryView("all", foreignKey);
	}
}
