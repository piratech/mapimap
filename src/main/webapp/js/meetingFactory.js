var size = new OpenLayers.Size(21, 25);
var offset = new OpenLayers.Pixel(-(size.w / 2), -size.h);
var crewIcon = new OpenLayers.Icon('img/marker.png', size, offset);
var squadIcon = new OpenLayers.Icon('img/marker-gold.png', size, offset);

function MeetingFactory(callback) {
	me = this;
	this.meetingCreatedCallback = callback;
	this.createMeeting = function(data) {
		for ( var i = 0; i < data.total_rows; i++) {
			var meeting = data.rows[i].value;
			meeting.position = createOSMLonLat(meeting.locationData.lon,
					meeting.locationData.lat);
			meeting.getHtml = me.createDefaultHtml;
			if (meeting.type == "CREW") {
				meeting.markerIcon = crewIcon.clone();
			}

			if (meeting.type == "SQUAD") {
				meeting.markerIcon = squadIcon.clone();
			}

			me.meetingCreatedCallback(meeting);
		}
	};

	this.createDefaultHtml = function() {
		return "<div><h1>" + this.name + "</h1><h2>" + this.type
				+ "</h2><div style='clear:both;'></div><ul><li>"
				+ this.locationData.address.road + "</li><li><a href='"
				+ this.wikiUrl + "' target='blank'>im Wiki</a></li></ul></div>";

	};
}