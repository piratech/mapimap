//needs map.js
$(document).ready(function() {
	$(".thatFades").fadeOut();
	
	$(".layerContainer").hover(function() {
		$(this).find(".thatFades").fadeIn();
	}, function() {
		$(this).find(".thatFades").fadeOut();
	});
	
	$("#submitAddressButton").click(function() {
		var address = {
			address : $("#address").val()
		};
		currentZoom = 15;
		geolocate(address, centerMap);
	});
});