document.addEventListener("DOMContentLoaded", () => {
	$(".extension > a").click(function(){
		 $(".extensionDetails").hide();
	    $(".extensionDetails",$(this).parent()).fadeIn(1000);
		return false;
	});
	$("body").click(function(){
	    $(".extensionDetails").fadeOut(1000);
	});
});