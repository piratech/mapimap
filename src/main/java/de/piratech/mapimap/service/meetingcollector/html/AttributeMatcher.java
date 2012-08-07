/**
 * 
 */
package de.piratech.mapimap.service.meetingcollector.html;

import org.apache.commons.lang3.StringUtils;
import org.htmlcleaner.TagNode;

/**
 * @author maria
 * 
 */
public class AttributeMatcher {

	public static final int EXACT_MATCH = 0;
	public static final int STARTS_WITH = 1;
	public static final int CONTAINT = 2;

	private String attributeName;
	private String attributeValue;
	private int matchMode;

	public AttributeMatcher(String attributeName, String attributeValue) {
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
	}

	public AttributeMatcher(String attributeName, String attributeValue,
			int matchMode) {
		this(attributeName, attributeValue);
		this.matchMode = matchMode;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public int getMatchMode() {
		return matchMode;
	}

	public void setMatchMode(int matchMode) {
		this.matchMode = matchMode;
	}

	protected boolean nodeHasAttribute(TagNode link) {
		return stringMatches(link.getAttributeByName(attributeName),
				attributeValue, matchMode);

	}

	private boolean stringMatches(String value, String matchValue, int _matchMode) {
		switch (_matchMode) {
		case EXACT_MATCH:
			return StringUtils.equals(value, matchValue);
		case STARTS_WITH:
			return StringUtils.startsWith(value, matchValue);
		case CONTAINT:
			return StringUtils.contains(value, matchValue);
		default:
			throw new IllegalArgumentException("match mode >" + _matchMode
					+ "< not handled");
		}
	}

}
