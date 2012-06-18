function BerlinCrew(name, wikiURL, address, time, callback) {
	
	this.name = name;
	this.wikiURL = wikiURL;
    this.coordinates = {lat:0, lon:0};
	this.address = address;
	this.time = time;
	this.initialized = callback;
	this.debug = false;
	this._init();
}

BerlinCrew.prototype._init = function() {
	this._getAddressPosition();
}

BerlinCrew.prototype._getAddressPosition= function() {
	if (typeof this.position == "undefined" && this.address !== null && typeof this.address != "undefined") {
		var me = this;
		$.ajax({
		  url: 'http://nominatim.openstreetmap.org/search?q='+this.address+'&format=json&polygon=1&addressdetails=1',
		  dataType:'json',
		  processData:false,
		}).success(function(data) {
			if(data.length > 0) {
                me.coordinates.lon = data[0].lon;
                me.coordinates.lat = data[0].lat;
				me.initialized(me);
			} else {
				me._debug("no location found for address "+me.address);
			}	
		});
	}
}

BerlinCrew.prototype._debug = function(msg) {
	if (this.debug) {
		console.log(msg);
	}
}

