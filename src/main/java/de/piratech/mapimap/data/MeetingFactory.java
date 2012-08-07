/**
 * 
 */
package de.piratech.mapimap.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author maria
 * 
 */
public class MeetingFactory<K extends Meeting> {

	private static final Logger LOG = LoggerFactory
			.getLogger(MeetingFactory.class);

	private Class<K> clazz;

	public MeetingFactory(Class<K> _clazz) {
		this.clazz = _clazz;
	}

	public K getInstance() {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			LOG.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
}
