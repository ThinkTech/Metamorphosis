var doc = product => {
	return {content : product.name};
};

module.init("product", product => product.name);