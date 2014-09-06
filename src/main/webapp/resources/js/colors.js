Colors = {};
Colors.names = {
	blue: "#0000ff",
	brown: "#a52a2a",
	cyan: "#00ffff",
	darkblue: "#00008b",
	darkcyan: "#008b8b",
	darkgreen: "#006400",
	darkkhaki: "#bdb76b",
	darkmagenta: "#8b008b",
	darkolivegreen: "#556b2f",
	darkorange: "#ff8c00",
	darkorchid: "#9932cc",
	darkred: "#8b0000",
	darksalmon: "#e9967a",
	darkviolet: "#9400d3",
	fuchsia: "#ff00ff",
	gold: "#ffd700",
	green: "#008000",
	indigo: "#4b0082",
	lime: "#00ff00",
	magenta: "#ff00ff",
	maroon: "#800000",
	navy: "#000080",
	olive: "#808000",
	orange: "#ffa500",
	pink: "#ffc0cb",
	purple: "#800080",
	violet: "#800080",
	red: "#ff0000"
};
Colors.random = function() {
	var result;
	var count = 0;
	for (var prop in this.names) {
		if (Math.random() < 1/++count) {
		   result = prop;
		}
	}
	return { name: result, rgb: this.names[result]};
};