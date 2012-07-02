function PiratePoint(name, wikiURL, callback) {
	
	this.name = name;
	this.wikiURL = wikiURL;
	this.addressURL = wikiURL+"/Treffpunkt";
	this.position;
	this.address;
	this.initialized = callback;
	this.debug = false;
	this._init();
}

PiratePoint.prototype._init = function() {
	this.addressURL = this.addressURL.replace(/ /g,"_");
	this._getAddress();
}

PiratePoint.prototype._getAddress = function() {
	if (typeof this.address == "undefined") {	
		var me = this;	
		$.get("/proxy.php?wikiressource="+this.addressURL,function(data, resultString, xhr) {
			var requestResult = xhr.responseText.replace(/src=".*"/g,"");
			var bodyContent = $(requestResult).find("#bodyContent")[0];
			var address = $(bodyContent).find(".address").html();
			me.address = address;
			me._debug("found address "+me.address+" for crew "+me.name);
			me._getAddressPosition();
		});

	}
	return this.address;
}

PiratePoint.prototype._getAddressPosition= function() {
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

PiratePoint.prototype._debug = function(msg) {
	if (this.debug) {
		console.log(msg);
	}
}

