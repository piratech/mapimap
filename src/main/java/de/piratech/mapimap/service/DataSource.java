/**
 *
 */
package de.piratech.mapimap.service;

import java.util.List;

import de.piratech.mapimap.data.Crew;
import de.piratech.mapimap.data.Squad;
import de.piratech.mapimap.data.Stammtisch;

/**
 * @author maria
 * 
 */
public interface DataSource {

	public List<Crew> getCrews();

	public void addCrew(Crew newCrew);

	public void delete(Crew crew);

	public List<Squad> getSquads();

	public void addSquad(Squad _newSquad);

	public void delete(Squad squad);

	public List<Stammtisch> getStammtische();

	public void delete(Stammtisch stammtich);

	public void addStammtisch(Stammtisch stammtisch);
}
