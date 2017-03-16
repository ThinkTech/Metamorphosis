var doc = order => {
	return {content : order.product};
};

module.init("order", order => order.product);