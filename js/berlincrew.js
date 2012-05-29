function BerlinCrew(name, wikiURL, address, time, callback) {
	
	this.name = name;
	this.wikiURL = wikiURL;
	this.position;
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
	if (typeof this.position == "undefined" && this.address != null && typeof this.address != "undefined") {
		var me = this;
		var result = $.ajax({
		  url: 'http://nominatim.openstreetmap.org/search?q='+this.address+'&format=json&polygon=1&addressdetails=1',
		  dataType:'json',
		  processData:false,
		}).success(function(data) {
			if(data.length > 0) {
				me.position = getPosition(data); //getPosition is in crewmap :(
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

