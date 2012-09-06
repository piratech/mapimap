package de.piratech.mapimap.data;

public enum MeetingType {
	STAMMTISCH("de.piratech.mapimap.data.Stammtisch"), CREW(
			"de.piratech.mapimap.data.Crew"), SQUAD("de.piratech.mapimap.data.Squad");

	private String className;

	private MeetingType(String className) {
		this.setClassName(className);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
