var doc = group => {
	return {content : group.name};
};

document.addEventListener("DOMContentLoaded", () => module.init("group", group => group.name));
