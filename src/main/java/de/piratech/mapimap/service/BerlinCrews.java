package de.piratech.mapimap.service;

import java.util.List;

import de.piratech.mapimap.data.Crew;

/**
 * @author maria
 *
 */
//@todo: deveth0@geirkairam: cant this be used as general interface (e.g. CrewProvider ? )
public interface BerlinCrews {

  public List<Crew> getCrews();
}
