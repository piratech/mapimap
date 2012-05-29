function PiratePointsCollection(url, ppSelector, crewLoadedCallback) {

	this.url = url;
	this.ppSelector = ppSelector;
	this.pps = [];
	this.crewLoadedCallback = crewLoadedCallback;
	this._getAddress();
	this.debug = false;
}

PiratePointsCollection.prototype._getAddress = function() {
	if (typeof this.address == "undefined") {	
		var me = this;	
		$.get("/proxy.php?wikiressource="+this.url,function(data, resultString, xhr) {
			//do not use data object --> ff will try to load images from wiki
			//console.log($(xhr.responseText)); leads to loading images, too. need to replace src tags >:(
			var requestResult = xhr.responseText.replace(/src=".*"/g,"");
			$(requestResult).find(me.ppSelector).each(function(index, element) {
				var pp = [$(this).html(), baseSourceURL+"/"+this.title];
				if (index < 50) {
					me.pps.push(pp);
				}
				me._debug("found crew "+pp[0]+" with url: "+pp[1]);
			});
			me.readCrews();	
		});
	}
}

PiratePointsCollection.prototype._debug = function(msg) {
	if (this.debug) {
		console.log(msg);
	}
}

PiratePointsCollection.prototype.readCrews = function() {
	if (this.pps != null && typeof this.pps != "undefined") {
		for(var i=0;i < this.pps.length;i++) {
			var pp = new PiratePoint(this.pps[i][0],this.pps[i][1],this.crewLoadedCallback);
		}
	}
}
