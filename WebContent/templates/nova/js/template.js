page.form.edit = () => {
	$('#create').magnificPopup('open');
	page.form.reset();
};

page.form.reset = () => {
	const wizard = $('#wizard').smartWizard('goToStep',1);
	const length = $("> div",wizard).length;
	for(var i=2;i<=length;i++) wizard.smartWizard('disableStep',i);
	setTimeout(() => $("input:eq(0)",$(".stepContainer div:eq(0)")).focus(), 100);
};

const onFinish = () => {
	page.form.submit();
	$('#create').magnificPopup('close');
	return true;
};

const onShowStep = (obj,context) => {
	$("input:eq(0)",$(".stepContainer div").eq(context.toStep-1)).focus();
	return true;
};

page.form.init = () => {
	$('#form').css("display","block").addClass("mfp-hide");
	head.load("templates/nova/js/jquery.smartWizard.js","templates/nova/js/jquery.magnific-popup.min.js", () => {
		$('#wizard').addClass("swMain").smartWizard({noForwardJumping:true,onShowStep:onShowStep,onFinish:onFinish});
		$('#create').magnificPopup({type:'inline'}).click(() => page.form.reset());
	});
};