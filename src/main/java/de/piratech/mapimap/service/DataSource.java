/**
 *
 */
package de.piratech.mapimap.service;

import java.util.List;

import de.piratech.mapimap.data.Crew;
import de.piratech.mapimap.data.Meeting;
import de.piratech.mapimap.data.Squad;
import de.piratech.mapimap.data.Stammtisch;
import de.piratech.mapimap.data.source.Source;

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

	public void addSource(Source source);

	public List<Source> getSources();

	public void updateSource(final Source source);

	public void delete(Source source);

	public void addMeeting(Meeting stammtisch);
}
