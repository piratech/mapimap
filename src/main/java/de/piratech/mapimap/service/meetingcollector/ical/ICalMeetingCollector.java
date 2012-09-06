/**
 * 
 */
package de.piratech.mapimap.service.meetingcollector.ical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.piratech.mapimap.data.LocationData;
import de.piratech.mapimap.data.Meeting;
import de.piratech.mapimap.data.MeetingFactory;
import de.piratech.mapimap.data.source.Source;
import de.piratech.mapimap.service.Geocoder;
import de.piratech.mapimap.service.meetingcollector.MeetingCollector;

/**
 * @author maria
 * 
 */
public class ICalMeetingCollector implements MeetingCollector {

	private static final Logger LOG = LoggerFactory
			.getLogger(ICalMeetingCollector.class);

	private Geocoder geocoder;
	private Source source;
	private MeetingFactory<Meeting> meetingFactory;

	@Override
	public List<Meeting> getMeetings() {
		try {
			String url = source.getUrl();
			CalendarBuilder calendarBuilder = new CalendarBuilder();
			HttpGet get = new HttpGet(url);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(get);
			Calendar calendar = calendarBuilder.build(response.getEntity()
					.getContent());
			ComponentList components = calendar.getComponents();
			List<Meeting> meetings = new ArrayList<Meeting>();
			for (Object object : components) {
				VEvent event = (VEvent) object;
				Meeting meeting = getMeeting(event);
				if (meeting != null) {
					LOG.info("found meeting {} with address {}", meeting.getName(),
							meeting.getLocationData().getAddress().getAddressString());
					meetings.add(meeting);
				}
			}
			return meetings;
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} catch (ParserException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	private Meeting getMeeting(VEvent event) {
		Meeting meeting = meetingFactory.getInstance();
		meeting.setName(event.getSummary().getValue());
		meeting.setWikiUrl(event.getUrl().getValue());
		meeting.setForeignKey(event.getUid().getValue());
		LocationData locationData = geocoder.getLocationData(event.getLocation()
				.getValue());
		if (locationData != null) {
			meeting.setLocationData(locationData);
			return meeting;
		} else {
			LOG.warn("Skip meeting because location not found");
			return null;
		}
	}

	@Override
	public void setGeocoder(Geocoder geocode) {
		this.geocoder = geocode;
	}

	@Override
	public void setSource(Source source) {
		this.source = source;
	}

	@Override
	public void setMeetingFactory(MeetingFactory<Meeting> meetingFactory) {
		this.meetingFactory = meetingFactory;
	}

}
