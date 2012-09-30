/**
 * 
 */
package de.piratech.mapimap.service.meetingcollector.ical;

import java.util.List;

import org.junit.Test;

import de.piratech.mapimap.data.Meeting;
import de.piratech.mapimap.data.MeetingFactory;
import de.piratech.mapimap.data.MeetingType;
import de.piratech.mapimap.data.source.Source;
import de.piratech.mapimap.service.NominatimGeocoderImpl;

/**
 * @author maria
 * 
 */
public class ICalMeetingCollectorTest {

	@Test
	public void test() {
		Source source = new Source();
		source.setMeetingType(MeetingType.STAMMTISCH);
		source.setUrl("http://api.piraten-bw.de/stammtisch/webcal/");
		MeetingFactory<Meeting> meetingFactory = new MeetingFactory<Meeting>(source);
		ICalMeetingCollector collector = new ICalMeetingCollector();
		collector.setSource(source);
		collector.setMeetingFactory(meetingFactory);
		collector.setGeocoder(new NominatimGeocoderImpl());
		List<Meeting> meetings = collector.getMeetings();
		System.out.println("ICalMeetingCollectorTest.test() meetings count: "
				+ meetings.size());
	}
}
