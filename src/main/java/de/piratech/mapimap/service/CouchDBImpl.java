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

  public CouchDBImpl(String url, String userName, String password, String database) {
    LOG.debug("DataSourceImpl(url >{}<, userName >{}<, password >{}<)", new Object[]{url, userName, password});
    try {
      HttpClient authenticatedHttpClient = new StdHttpClient.Builder().url(url).username(userName).password(password).build();
      StdCouchDbInstance couchDbInstance = new StdCouchDbInstance(authenticatedHttpClient);
      CouchDbConnector couchDbConnector = couchDbInstance.createConnector(database, false);
      crewRepo = new CrewsRepository(Crew.class, couchDbConnector);
    } catch (MalformedURLException e) {
      LOG.debug("DataSourceImpl(url >{}<, userName >{}<, password >{}<)");
      //@todo: deveth0@geirkairam: useless, already logged
      e.printStackTrace();
    }
  }

  @Override
  public List<Crew> getCrews() {
    return crewRepo.getAll();
  }

  @Override
  public void addCrew(Crew newCrew) {
    //@todo: deveth0@geirkairam: There should be a timestamp, when the crew has been updated for the last time
    if (!crewRepo.crewExists(newCrew)) {
      LOG.info("addging crew {} to database", newCrew.getName());
      crewRepo.add(newCrew);
    }
    else {
      updateCrew(newCrew);
    }
  }

  public void updateCrew(Crew crew) {
    LOG.info("updating crew  {}", crew.getName());
    // Update crew only if something has changed
    Crew crewInDB = crewRepo.findByWikiUrl(crew.getWikiUrl()).get(0);
    if (StringUtils.equals(crewInDB.getCheckSum(), crew.getCheckSum())) {
      crewRepo.update(crew);
    }
  }

  @Override
  public void delete(Crew crew) {
    crewRepo.remove(crew);
  }

  @Override
  public List<Squad> getSquads() {
    throw new UnsupportedOperationException("not implemented yet");
  }

  @Override
  public void addSquad(Crew newSquad) {
    throw new UnsupportedOperationException("not implemented yet");
  }

  @Override
  public void delete(Squad squad) {
    throw new UnsupportedOperationException("not implemented yet");
  }
}
