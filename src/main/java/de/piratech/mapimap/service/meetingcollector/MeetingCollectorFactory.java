/**
 * 
 */
package de.piratech.mapimap.service.meetingcollector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.piratech.mapimap.data.Meeting;
import de.piratech.mapimap.data.MeetingFactory;
import de.piratech.mapimap.data.source.Source;
import de.piratech.mapimap.service.Geocoder;

/**
 * @author maria
 * 
 */
public class MeetingCollectorFactory {

	public static MeetingCollector getInstance(Source source, Geocoder geocoder) {
		try {
			Class<?> clazz = Class.forName(source.getSourceType().getClassName());
			MeetingCollector collector = (MeetingCollector) clazz.newInstance();
			Method setGeocoder = clazz.getMethod("setGeocoder", Geocoder.class);
			setGeocoder.invoke(collector, geocoder);
			Method setSource = clazz.getMethod("setSource", Source.class);
			setSource.invoke(collector, source);
			Method setMeetingFactory = clazz.getMethod("setMeetingFactory", MeetingFactory.class);
			setMeetingFactory.invoke(collector, new MeetingFactory<Meeting>(source));
			return collector;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
