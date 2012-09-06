/**
 *
 */
package de.piratech.mapimap.service;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;

import de.piratech.mapimap.data.source.Source;

/**
 * @author maria
 *
 */
public class SourceRepository extends CouchDbRepositorySupport<Source> {

  protected SourceRepository(Class<Source> _type, CouchDbConnector _db) {
    super(_type, _db);
  }

  public List<Source> getSources() {
    return queryView("source/all");
  }

  public boolean sourceExists(final Source _newCrew) {
    List<Source> existingCrews = findByUrl(_newCrew.getUrl());
    if (!existingCrews.isEmpty()) {
      _newCrew.setId(existingCrews.get(0).getId());
      _newCrew.setRevision(existingCrews.get(0).getRevision());
      return true;
    }
    return false;
  }

  @GenerateView
  public List<Source> findByUrl(final String _url) {
    return queryView("all", _url);
  }
}
