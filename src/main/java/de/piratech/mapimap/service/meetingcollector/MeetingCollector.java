package de.piratech.mapimap.service.meetingcollector;

import java.util.List;

import de.piratech.mapimap.data.Meeting;
import de.piratech.mapimap.data.MeetingFactory;
import de.piratech.mapimap.data.source.Source;
import de.piratech.mapimap.service.Geocoder;

/**
 * @author maria
 * 
 */
public interface MeetingCollector {

	public List<Meeting> getMeetings();

	public void setGeocoder(Geocoder geocode);

	public void setSource(Source source);

	public void setMeetingFactory(MeetingFactory<Meeting> meetingFactory);

}
