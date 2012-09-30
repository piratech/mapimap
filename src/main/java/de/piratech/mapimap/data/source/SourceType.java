package de.piratech.mapimap.data.source;

public enum SourceType {
	HTMLTABLE(
			"de.piratech.mapimap.service.meetingcollector.html.HTMLTableMeetingCollector"), HTMLATTRIBUTEPAGE(
			"de.piratech.mapimap.service.meetingcollector.html.HTMLAttributeMeetingCollector"), HTMLLINKLIST(
			"de.piratech.mapimap.service.meetingcollector.html.HTMLMeetingCollectorLinkList"), ICAL(
			"de.piratech.mapimap.service.meetingcollector.ical.ICalMeetingCollector");

	private String className;

	private SourceType(String className) {
		this.setClassName(className);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
