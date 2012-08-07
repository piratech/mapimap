package de.piratech.mapimap.service.meetingcollector.html;


public class HTMLSource {
	public AttributeMatcher meetingIdentifier;
	public AttributeMatcher nameIdentifier;
	public AttributeMatcher addressIdentifier;
	public String _urlString;

	public HTMLSource(AttributeMatcher meetingIdentifier,
			AttributeMatcher nameIdentifier, AttributeMatcher addressIdentifier,
			String _urlString) {
		this.meetingIdentifier = meetingIdentifier;
		this.nameIdentifier = nameIdentifier;
		this.addressIdentifier = addressIdentifier;
		this._urlString = _urlString;
	}

	public AttributeMatcher getMeetingIdentifier() {
		return meetingIdentifier;
	}

	public void setMeetingIdentifier(AttributeMatcher meetingIdentifier) {
		this.meetingIdentifier = meetingIdentifier;
	}

	public AttributeMatcher getNameIdentifier() {
		return nameIdentifier;
	}

	public void setNameIdentifier(AttributeMatcher nameIdentifier) {
		this.nameIdentifier = nameIdentifier;
	}

	public AttributeMatcher getAddressIdentifier() {
		return addressIdentifier;
	}

	public void setAddressIdentifier(AttributeMatcher addressIdentifier) {
		this.addressIdentifier = addressIdentifier;
	}

	public String get_urlString() {
		return _urlString;
	}

	public void set_urlString(String _urlString) {
		this._urlString = _urlString;
	}

}