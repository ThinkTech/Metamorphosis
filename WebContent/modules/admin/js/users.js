var doc = user => {
	return {content : user.firstName+" "+user.lastName};
};
document.addEventListener("DOMContentLoaded", () => module.init("user", user => user.firstName+" "+user.lastName));