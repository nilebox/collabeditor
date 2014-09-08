var queue = function() {
	var elements = [];

	this.enqueue = function(element) {
		elements.push(element);                       
	};
	this.dequeue = function() {
		return elements.shift();                                                
	};
	this.peek = function() {
		return elements[0];                  
	};
	this.last = function() {
		return elements[elements.length-1];
	};	
	this.size = function() {
		return elements.length;
	};
	this.get = function(index) {
		return elements[index];
	};
};