OpenLayers.Layer.Markers.prototype.markerWithLonLatExists = function(lonlat) {
	for ( var i = 0; i < this.markers.length; i++) {
		if (this.markers[i].lonlat.equals(lonlat)) {
			return true;
		}
	}
	return false;
};

var service = "http://nominatim.openstreetmap.org/search";

function showPopup(map, position, content) {
	var anchor;
	var popup = new OpenLayers.Popup.FramedCloud(null, position,
			new OpenLayers.Size(200, 200), content, anchor, true);
	map.addPopup(popup);
}

function geolocate(address, callback) {
	$.ajax(
			{
				url : service+'?q='
						+ encodeURIComponent(address)
						+ '&format=json&polygon=1&addressdetails=1',
				dataType : 'json',
				processData : false
			}).success(function(data) {
		callback(data);
	});
}

function createOSMLonLat(lon, lat) {
	var fromProjection = new OpenLayers.Projection("EPSG:4326"); // Transform
	// from WGS 1984
	var toProjection = new OpenLayers.Projection("EPSG:900913"); // to
																	// Spherical
	// Mercator
	// Projection
	var position = new OpenLayers.LonLat(lon, lat).transform(fromProjection,
			toProjection);
	return position;
}

