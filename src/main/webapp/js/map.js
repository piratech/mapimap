var baseSourceURL = "http://wiki.piratenpartei.de";
var size = new OpenLayers.Size(21, 25);
var offset = new OpenLayers.Pixel(-(size.w / 2), -size.h);
var crewIcon = new OpenLayers.Icon('img/marker.png', size, offset);
var squadIcon = new OpenLayers.Icon('img/marker-gold.png', size, offset);
var defaultLonLat = getCoordinates(10.451526, 51.165691);
var urlParameter = getUrlVars();
var currentZoom = 7;
var map;
var crewLayer = new OpenLayers.Layer.Markers("Crews");
crewLayer.markerWithLonLatExists = function(lonlat) {
	for ( var i = 0; i < this.markers.length; i++) {
		if (this.markers[i].lonlat.equals(lonlat)) {
			return true;
		}
	}
	return false;
};
var crews = new Array();
crews.getCrewsWithPosition = function(position) {
	var returnArray = new Array();
	for ( var i = 0; i < this.length; i++) {
		var crew = this[i];
		if (crew.position.equals(position)) {
			returnArray.push(crew);
		}
	}
	return returnArray;
};

$(document)
		.ready(
				function() {

					// map zeug
					var options = {
						theme : "OpenLayers/theme/default/style.css"
					};
					map = new OpenLayers.Map("map_canvas", options);
					var osm = new OpenLayers.Layer.OSM();
					map.addLayer(osm);
					map.addLayer(crewLayer);
					setMapCenter();

					$.ajaxSetup({
						scriptCharset : "utf-8",
						contentType : "application/json; charset=utf-8"
					});
					$
							.getJSON(
									'http://piratech.iriscouch.com/mapimap/_design/Crew/_view/all?callback=?',
									function(data) {
										for ( var i = 0; i < data.total_rows; i++) {
											var crew = data.rows[i].value;
											crew.position = getCoordinates(crew.locationData.lon,
													crew.locationData.lat);
											crewLoaded(crew);
										}
									});

					$
							.getJSON(
									'http://piratech.iriscouch.com/mapimap/_design/Squad/_view/all?callback=?',
									function(data) {
										for ( var i = 0; i < data.total_rows; i++) {
											var squad = data.rows[i].value;
											squad.position = getCoordinates(squad.locationData.lon,
													squad.locationData.lat);
											squadLoaded(squad);
										}
									});

					$
							.getJSON(
									'http://piratech.iriscouch.com/mapimap/_design/Stammtisch/_view/all?callback=?',
									function(data) {
										for ( var i = 0; i < data.total_rows; i++) {
											var squad = data.rows[i].value;
											squad.position = getCoordinates(squad.locationData.lon,
													squad.locationData.lat);
											crewLoaded(squad);
										}
									});

					$("#editbox").fadeOut();

					// edit box zeug
					$("#editLayer").hover(function() {
						$("#editbox").fadeIn();
					}, function() {
						$("#editbox").fadeOut();
					});

					$("#submitAddressButton").click(function() {
						var address = {
							address : $("#address").val()
						};
						currentZoom = 15;
						geolocate(address, centerMap);
					});
				});

function setMapCenter() {
	getInitialZoom();
	var lon = urlParameter["lon"];
	var lat = urlParameter["lat"];

	if (typeof lon != "undefined" && typeof lat != "undefined") {
		map.setCenter(getCoordinates(lon, lat), currentZoom);
	} else {
		var address = urlParameter["address"];
		if (typeof address != "undefined") {
			return geolocate({address:address}, centerMap);
		} else {
			map.setCenter(defaultLonLat,currentZoom);
		}
	}
}

function getInitialZoom() {
	if (typeof urlParameter["zoom"] != "undefined") {
		currentZoom = urlParameter["zoom"];
	}
}

var crewLoaded = function(crew) {
	if (!crewLayer.markerWithLonLatExists(crew.position)) {
		var marker = new OpenLayers.Marker(crew.position, crewIcon.clone());
		marker.events.on({
			"click" : function() {
				showPopup(crew.position)
			}
		});
		crewLayer.addMarker(marker);
	}
	crews.push(crew);
}

var squadLoaded = function(crew) {
	if (!crewLayer.markerWithLonLatExists(crew.position)) {
		var marker = new OpenLayers.Marker(crew.position, squadIcon.clone());
		marker.events.on({
			"click" : function() {
				showPopup(crew.position)
			}
		});
		crewLayer.addMarker(marker);
	}
	crews.push(crew);
}

function showPopup(position, anchor) {
	var crewsWithPosition = crews.getCrewsWithPosition(position);
	var popupHTML = "";
	for ( var i = 0; i < crewsWithPosition.length; i++) {
		popupHTML = popupHTML + "<div><h1>" + crewsWithPosition[i].name
				+ "</h1><h2>" + crewsWithPosition[i].type
				+ "</h2><div style='clear:both;'></div><ul><li>"
				+ crewsWithPosition[i].locationData.address.road + "</li><li><a href='"
				+ crewsWithPosition[i].wikiUrl
				+ "' target='blank'>im Wiki</a></li></ul></div>"
	}
	var popup = new OpenLayers.Popup.FramedCloud(null, position,
			new OpenLayers.Size(200, 200), popupHTML, anchor, true);
	map.addPopup(popup);
}

var centerMap = function(locationData) {
	var position = getPosition(locationData);
	map.setCenter(position, currentZoom);
}

function getCoordinates(lon, lat) {
	var fromProjection = new OpenLayers.Projection("EPSG:4326"); // Transform
	// from WGS 1984
	var toProjection = new OpenLayers.Projection("EPSG:900913"); // to Spherical
	// Mercator
	// Projection
	var position = new OpenLayers.LonLat(lon, lat).transform(fromProjection,
			toProjection);
	return position;
}

function getPosition(data) {
	var fromProjection = new OpenLayers.Projection("EPSG:4326"); // Transform
	// from WGS 1984
	var toProjection = new OpenLayers.Projection("EPSG:900913"); // to Spherical
	// Mercator
	// Projection
	var position = new OpenLayers.LonLat(data[0].lon, data[0].lat).transform(
			fromProjection, toProjection);
	return getCoordinates(data[0].lon, data[0].lat);
}

function getUrlVars() {
	var vars = [], hash;
	var hashes = window.location.href
			.slice(window.location.href.indexOf('?') + 1).split('&');
	for ( var i = 0; i < hashes.length; i++) {
		hash = hashes[i].split('=');
		vars.push(hash[0]);
		vars[hash[0]] = hash[1];
	}
	return vars;
}

function geolocate(obj, callback) {
	$.ajax(
			{
				url : 'http://nominatim.openstreetmap.org/search?q='
						+ encodeURIComponent(obj.address)
						+ '&format=json&polygon=1&addressdetails=1',
				dataType : 'json',
				processData : false
			}).success(function(data) {
		callback(data);
	});
}

function justReturn(data) {
	data;
}