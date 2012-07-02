function BerlinCrewsCollection(url, ppSelector, crewLoadedCallback) {
	
	this.url = url;
	this.ppSelector = ppSelector;
	this.pps = [];
	this.crewLoadedCallback = crewLoadedCallback;
	this._getAddress();
	this.debug = false;
}

BerlinCrewsCollection.prototype._getAddress = function() {
	if (typeof this.address == "undefined") {	
		var me = this;	
		$.get("/proxy.php?wikiressource="+this.url,function(data, resultString, xhr) {
			//do not use data object --> ff will try to load images from wiki
			//console.log($(xhr.responseText)); leads to loading images, too. need to replace src tags >:(
			var requestResult = xhr.responseText.replace(/src=".*"/g,"");
			$(requestResult).find(me.ppSelector).each(function(index, element) {
				var crew = $(this);				
				var address =crew.find(".address").html();
				var name = crew.find(".name").html();
				var time = crew.find(".time").html();
				new BerlinCrew(name, baseSourceURL+"/BE:Crews/"+name, address, time, me.crewLoadedCallback);
			});
		});
	}
}

BerlinCrewsCollection.prototype._debug = function(msg) {
	if (this.debug) {
		console.log(msg);
	}
}
