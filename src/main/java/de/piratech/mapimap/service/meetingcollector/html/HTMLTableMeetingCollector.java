/**
 * 
 */
package de.piratech.mapimap.service.meetingcollector.html;

import java.util.List;

import org.htmlcleaner.TagNode;

import de.piratech.mapimap.data.MeetingFactory;
import de.piratech.mapimap.service.Geocoder;

/**
 * @author maria
 * 
 */
public class HTMLTableMeetingCollector extends AbstractHTMLMeetingCollector {

	public HTMLTableMeetingCollector(HTMLSource htmlsource, Geocoder _geocoder,
			MeetingFactory<?> meetingFactory) {
		super(htmlsource, _geocoder, meetingFactory);
	}

	@Override
	protected List<TagNode> getMeetingTagNodes() {
		TagNode table = getNodeWithAttribute(getTagNode(),
				this.htmlSource.getAttributeIdentifier(HTMLSource.MEETING_TAG));
		return getRows(table);
	}

	protected String getName(TagNode meetingNode) {
		return getColumnValue(meetingNode, HTMLSource.NAME_TAG);
	}

	@Override
	protected String getLon(TagNode meeting) {
		return getColumnValue(meeting, HTMLSource.LON_TAG);
	}

	@Override
	protected String getLat(TagNode meeting) {
		return getColumnValue(meeting, HTMLSource.LAT_TAG);
	}

	@Override
	protected String getRoad(TagNode meeting) {
		return getColumnValue(meeting, HTMLSource.STREET_TAG);
	}

	@Override
	protected String getZip(TagNode meeting) {
		return getColumnValue(meeting, HTMLSource.ZIP_TAG);
	}

	@Override
	protected String getCity(TagNode meeting) {
		return getColumnValue(meeting, HTMLSource.TOWN_TAG);
	}

	@Override
	protected String getAddress(TagNode meeting) {
		return getColumnValue(meeting, HTMLSource.ADDRESS_TAG);
	}

	@Override
	protected String getURL(TagNode meeting) {
		TagNode column = getColumn(getColumns(meeting), HTMLSource.URL_TAG);
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

	private TagNode getColumn(List<?> row, String columnName) {
		if (row != null && !row.isEmpty()) {
			int columnNumber = this.htmlSource.getTableColumn(columnName);
			if (columnNumber != -1) {
				return (TagNode) row.get(columnNumber);
			} else {
				// not column defined
			}
		}
		return null;
	}

	private String getColumnValue(TagNode meeting, String columnName) {
		return getNodeValue(getColumn(getColumns(meeting), columnName));
	}
}
