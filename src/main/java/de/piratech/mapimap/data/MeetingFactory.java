/**
 * 
 */
package de.piratech.mapimap.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.piratech.mapimap.data.source.Source;

/**
 * @author maria
 * 
 */
public class MeetingFactory<K extends Meeting> {

	private static final Logger LOG = LoggerFactory
			.getLogger(MeetingFactory.class);

	private Class<K> clazz;

	private Source source;

	public MeetingFactory(Class<K> _clazz) {
		this.clazz = _clazz;
	}

	public MeetingFactory(Source source) {
		this.source = source;
	}

	public K getInstance() {
		if (source != null) {
			try {
				Class<?> clazz = Class.forName(source.getMeetingType().getClassName());
				return (K) clazz.newInstance();
			} catch (ClassNotFoundException e) {
				LOG.error(e.getMessage(), e);
			} catch (InstantiationException e) {
				LOG.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			}
		} else {
			try {
				return clazz.newInstance();
			} catch (InstantiationException e) {
				LOG.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return null;
	}
}
