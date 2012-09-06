package de.piratech.mapimap.data;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public abstract class Meeting {


	// @todo: deveth0@geirkairam: propably a map-url would be useful for external
	// apps
	@JsonProperty("_id")
	private String id;
	@JsonProperty("_rev")
	private String revision;
	private String name;
	private String wikiUrl;
	private LocationData locationData;
	private String checkSum;

	public String getId() {
		return id;
	}

	public void setId(String s) {
		id = s;
	}

	public String getRevision() {
		return revision;
	}

	@JsonProperty("_rev")
	public void setRevision(String s) {
		revision = s;
	}

	public void setName(String s) {
		name = s;
	}

	public String getName() {
		return name;
	}

	public String getWikiUrl() {
		return wikiUrl;
	}

	public void setWikiUrl(String url) {
		this.wikiUrl = url;
	}

	public LocationData getLocationData() {
		return locationData;
	}

	public void setLocationData(LocationData locationData) {
		this.locationData = locationData;
	}

	abstract public MeetingType getType();

	abstract public void setType(MeetingType type);

	public String getCheckSum() {
		if (StringUtils.isBlank(checkSum)) {
			checkSum = DigestUtils.md5Hex(getName() + getWikiUrl()
					+ getLocationData().toString() + getType().toString());
		}
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}
}
