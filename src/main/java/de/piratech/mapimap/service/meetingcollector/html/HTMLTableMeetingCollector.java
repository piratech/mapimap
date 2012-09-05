/**
 * 
 */
package de.piratech.mapimap.service.meetingcollector.html;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.htmlcleaner.TagNode;

import de.piratech.mapimap.data.source.SourceInformationIdentifierType;

/**
 * @author maria
 * 
 */
public class HTMLTableMeetingCollector extends AbstractHTMLMeetingCollector {

	@Override
	protected List<TagNode> getMeetingTagNodes() {
		TagNode table = getNodeWithAttribute(getTagNode(),
				this.htmlSource.getSourceInformationIdentifier(
						SourceInformationIdentifierType.MEETING, AttributeMatcher.class));
		return getRows(table);
	}

	protected String getName(TagNode meetingNode) {
		return getColumnValue(meetingNode, SourceInformationIdentifierType.NAME);
	}

	@Override
	protected String getLon(TagNode meeting) {
		return getColumnValue(meeting, SourceInformationIdentifierType.LON);
	}

	@Override
	protected String getLat(TagNode meeting) {
		return getColumnValue(meeting, SourceInformationIdentifierType.LAT);
	}

	@Override
	protected String getRoad(TagNode meeting) {
		return getColumnValue(meeting, SourceInformationIdentifierType.STREET);
	}

	@Override
	protected String getZip(TagNode meeting) {
		return getColumnValue(meeting, SourceInformationIdentifierType.ZIP);
	}

	@Override
	protected String getCity(TagNode meeting) {
		return getColumnValue(meeting, SourceInformationIdentifierType.TOWN);
	}

	@Override
	protected String getAddress(TagNode meeting) {
		return getColumnValue(meeting, SourceInformationIdentifierType.ADDRESS);
	}

	@Override
	protected String getURL(TagNode meeting) {
		TagNode column = getColumn(getColumns(meeting),
				SourceInformationIdentifierType.URL);
		if (column != null) {
			TagNode link = (TagNode) column.getChildren().get(0);
			return link.getAttributeByName("href");
		}
		return null;
	}

	private List<TagNode> getRows(TagNode tableNode) {
		List<TagNode> rowNodes = tableNode.getChildren();
		if (!rowNodes.isEmpty()) {
			TagNode firstChild = (TagNode) rowNodes.get(0);
			if (firstChild.getName().equals("tr")) {
				return rowNodes;
			} else if (firstChild.getName().equals("tbody")) {
				return firstChild.getChildren();
			} else {
				throw new IllegalStateException("node type >" + tableNode.getName()
						+ "< not handled ");
			}
		}
		return null;
	}

	private List<TagNode> getColumns(TagNode row) {
		List<TagNode> columnNodes = row.getChildren();
		if (!columnNodes.isEmpty()) {
			TagNode firstColumn = (TagNode) columnNodes.get(0);
			if (firstColumn.getName().equals("td")) {
				return columnNodes;
			}
		}
		return null;
	}

	private TagNode getColumn(List<?> row,
			SourceInformationIdentifierType columnName) {
		if (row != null && !row.isEmpty()) {
			Integer columnNumber = this.htmlSource.getSourceInformationIdentifier(
					columnName, Integer.class);
			if (columnNumber != null && !columnNumber.equals(-1)) {
				return (TagNode) row.get(columnNumber);
			} else {
				// not column defined
			}
		}
		return null;
	}

	private String getColumnValue(TagNode meeting,
			SourceInformationIdentifierType columnName) {
		String nodeValue = getNodeValue(getColumn(getColumns(meeting), columnName));
		if (nodeValue != null) {
			return StringUtils.chomp(nodeValue);
		}
		return null;
	}
}
