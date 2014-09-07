Colors = {};
Colors.names = [
	/*red*/ "#ff0000",		
	/*blue*/ "#0000ff",
	/*orange*/ "#ffa500",	
	/*green*/ "#008000",	
	/*darkcyan*/ "#008b8b",
	/*darkkhaki*/ "#bdb76b",
	/*darkmagenta*/ "#8b008b",
	/*darkolivegreen*/ "#556b2f",
	/*darkorange*/ "#ff8c00",
	/*darkgreen*/ "#006400",		
	/*darkred*/ "#8b0000",
	/*darksalmon*/ "#e9967a",
	/*darkviolet*/ "#9400d3",
	/*fuchsia*/ "#ff00ff",
	/*lime*/ "#00ff00",
	/*navy*/ "#000080",
	/*olive*/ "#808000",
	/*purple*/ "#800080"
];
Colors.last = -1;
Colors.next = function() {
	this.last++;
	return this.names[this.last];
};