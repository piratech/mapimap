Array.prototype.getmeetingsWithPosition = function(position) {
	var returnArray = new Array();
	for ( var i = 0; i < this.length; i++) {
		var meeting = this[i];
		if (meeting.position.equals(position)) {
			returnArray.push(meeting);
		}
	}
	return returnArray;
};

function Mapimap(data) {
	this.tag = data.tag;
	this.defaultLonLat = data.defaultLonLat;
	this.currentZoom = data.defaultZoom;
	this.urlParameter = data.urlParameter;
	this.map;
	this.meetingLayer;
	this.meetings;

	this.init = function() {
		this.meetings = new Array();
		mapimap = this;
		this.map = new OpenLayers.Map(this.tag);
		var osmLayer = new OpenLayers.Layer.OSM();
		this.meetingLayer = new OpenLayers.Layer.Markers("Meetings");
		this.map.addLayer(osmLayer);
		this.map.addLayer(this.meetingLayer);
		if (typeof this.urlParameter["zoom"] != "undefined") {
			this.currentZoom = this.urlParameter["zoom"];
		}

		if (typeof this.urlParameter["lon"] != "undefined"
				&& typeof urlParameter["lat"] != "undefined") {
			map.setCenter(createOSMLonLat(this.urlParameter["lon"],
					urlParameter["lat"]), this.currentZoom);
		} else {
			var address = this.urlParameter["address"];
			if (typeof address != "undefined") {
				return geolocate(address, this.centerMap);
			} else {
				this.map.setCenter(this.defaultLonLat, this.currentZoom);
			}
		}
	};

	this.meetingLoaded = function(meeting) {
		if (!mapimap.meetingLayer.markerWithLonLatExists(meeting.position)) {
			var marker = new OpenLayers.Marker(meeting.position,
					meeting.markerIcon);
			marker.events.on({
				"click" : function() {
					var meetingsWithPosition = mapimap.meetings
							.getmeetingsWithPosition(meeting.position);
					var popupHTML = "";
					for ( var i = 0; i < meetingsWithPosition.length; i++) {
						popupHTML = popupHTML
								+ meetingsWithPosition[i].getHtml();
					}
					showPopup(mapimap.map, meeting.position, popupHTML)
				}
			});
			mapimap.meetingLayer.addMarker(marker);
		}
		mapimap.meetings.push(meeting);
	};

	this.centerMap = function(locationData) {
		mapimap.map.setCenter(createOSMLonLat( locationData[0].lon, locationData[0].lat), mapimap.currentZoom);
	}
}