package de.piratech.mapimap.service.meetingcollector.html;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.piratech.mapimap.data.source.Source;

public class HTMLSource extends Source {
	public static final String ADDRESS_TAG = "address";
	public static final String LAT_TAG = "lat";
	public static final String LON_TAG = "lon";
	public static final String NAME_TAG = "name";
	public static final String MEETING_TAG = "meeting";
	public static final String STREET_TAG = "street";
	public static final String ZIP_TAG = "zip";
	public static final String TOWN_TAG = "town";
	public static final String URL_TAG = "url";
	private String urlString;
	private boolean table;
	private Map<String, Object> informationIdentifier;

	public HTMLSource(Map<String, Object> informationIdentifier, String _urlString) {
		this.informationIdentifier = informationIdentifier;
		this.urlString = _urlString;
	}

	public boolean isTable() {
		return table;
	}

	public void setTable(boolean table) {
		this.table = table;
	}

	public String getUrlString() {
		return urlString;
	}

	public void setUrlString(String _urlString) {
		this.urlString = _urlString;
	}

	public AttributeMatcher getAttributeIdentifier(String key) {
		return (AttributeMatcher) informationIdentifier.get(key);
	}

	public int getTableColumn(String key) {
		if (StringUtils.isNotEmpty(key)) {
			if (this.informationIdentifier.get(key) != null) {
				return (Integer) this.informationIdentifier.get(key);
			}
		}
		return -1;
	}

}