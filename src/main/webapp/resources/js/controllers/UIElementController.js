/*!
 * Controller which observes changes in UI
 * and provides API for CollaborationController to change UI
 * Copyright 2014 nilebox@gmail.com
*/

function UIElementController(container, textArea, fakeArea, titleArea, userArea, reloadModal) {
	this.container = container;
	this.textArea = textArea;
	this.fakeArea = fakeArea[0].firstChild;
	this.titleArea = titleArea;
	this.userArea = userArea;
	this.reloadModal = reloadModal;
	
	this.oldVal = this.getText();
	this.oldTitle = this.titleArea.text();
	this.oldCaretPosition = 0;
	this.titleArea.on('save', this.handleTitleChanged.bind(this));
	// handle text changes
	this.textArea.on('change keyup keydown cut paste textInput', this.handleTextChanged.bind(this));
	// handle caret position changes
	var timer = 0;
	var caretHandler = function() {
		clearTimeout(timer);
		timer = setTimeout(this.handleCaretChanged.bind(this), 10);
	};
	this.textArea.on("input keydown keyup propertychange click paste cut copy mousedown mouseup change",
		caretHandler.bind(this));
	
	this.clientColors = {};
	this.clientCarets = {};
	this.clientBadges = {};
}

UIElementController.prototype.onTitleChanged = function(callback) {
	// Make sure the callback is a function
    if (typeof callback === "function")
		this.titleCallback = callback;
};

UIElementController.prototype.onRemove = function(callback) {
	// Make sure the callback is a function
    if (typeof callback === "function")
		this.removeCallback = callback;
};

UIElementController.prototype.onInsert = function(callback) {
	// Make sure the callback is a function
    if (typeof callback === "function")
		this.insertCallback = callback;
};

UIElementController.prototype.onCaretChanged = function(callback) {
	// Make sure the callback is a function
    if (typeof callback === "function")
		this.caretCallback = callback;
};

UIElementController.prototype.handleTitleChanged = function(e, params) {
	var newTitle = params.newValue;
	if (newTitle === this.oldTitle) {
		return; //check to prevent multiple simultaneous triggers
	}
	
	this.oldTitle = newTitle;
	this.titleCallback(newTitle);
};

UIElementController.prototype.handleTextChanged = function() {
	var newVal = this.getText();
	if (newVal === this.oldVal) {
		return; //check to prevent multiple simultaneous triggers
	}

	var commonStart = 0;
	while (this.oldVal.charAt(commonStart) === newVal.charAt(commonStart)) {
		commonStart++;
	}

	var commonEnd = 0;
	while (this.oldVal.charAt(this.oldVal.length - 1 - commonEnd) === newVal.charAt(newVal.length - 1 - commonEnd) &&
			commonEnd + commonStart < this.oldVal.length && commonEnd + commonStart < newVal.length) {
		commonEnd++;
	}

	if (this.oldVal.length !== commonStart + commonEnd) {
		this.removeCallback(commonStart, this.oldVal.length - commonStart - commonEnd);
	}
	if (newVal.length !== commonStart + commonEnd) {
		this.insertCallback(commonStart, newVal.slice(commonStart, newVal.length - commonEnd));
	}
	this.oldVal = newVal;
};

UIElementController.prototype.handleCaretChanged = function() {
	var caretPosition = UIElementController.getCaretPosition(this.textArea[0]);
	if (this.oldCaretPosition === caretPosition)
		return;
	this.oldCaretPosition = caretPosition;
	this.caretCallback(caretPosition);
};

UIElementController.prototype.getText = function() {
	return this.textArea.val();
};

UIElementController.prototype.setText = function(newText) {
	var elem = this.textArea[0]; //get DOM element from jquery object
	// Fixate the window's scroll while we set the element's value. Otherwise
	// the browser scrolls to the element.
	var scrollTop = elem.scrollTop;
	this.handleTextChanged();	
	elem.value = newText;
	this.oldVal = elem.value;
	if (elem.scrollTop !== scrollTop)
		elem.scrollTop = scrollTop;
};

UIElementController.prototype.getTextSelection = function() {
	var elem = this.textArea[0]; //get DOM element from jquery object
	return [elem.selectionStart, elem.selectionEnd];
};

UIElementController.prototype.setTextSelection = function(newSelection) {
	var elem = this.textArea[0]; //get DOM element from jquery object
	// Setting the selection moves the cursor. We'll just have to let your
	// cursor drift if the element isn't active, though usually users don't
	// care.
	if (newSelection && window.document.activeElement === elem) {
		elem.selectionStart = newSelection[0];
		elem.selectionEnd = newSelection[1];
	}
};

UIElementController.prototype.setTitle = function(newTitle) {
	this.oldTitle = newTitle;
	this.titleArea.editable('setValue', newTitle);
};

UIElementController.prototype.showRemoteCaret = function(clientId, username, caretPosition) {
	this.addUserBadge(clientId, username);
	this.setCaretPosition(clientId, caretPosition);
};

UIElementController.prototype.removeRemoteClient = function(clientId) {
	this.removeUserBadge(clientId);
	this.removeCaret(clientId);
};

UIElementController.prototype.getClientCaret = function(clientId) {
	if (clientId in this.clientCarets) {
		return this.clientCarets[clientId];
	}
	var caret = UIElementController.createCaret(this.getClientColor(clientId));
	//add created cursor to DOM tree
	this.container.append(caret);
	this.clientCarets[clientId] = caret;
	return caret;
};

UIElementController.createCaret = function(color) {
	return $('<div></div>')
			.addClass('fake_caret blink');
};

UIElementController.prototype.getClientColor = function(clientId) {
	if (clientId in this.clientColors) {
		return this.clientColors[clientId];
	}
	var color = Colors.next();
	this.clientColors[clientId] = color;
	return color;
};

UIElementController.prototype.addUserBadge = function(clientId, username) {
	if (clientId in this.clientBadges)
		return; //already exists
	var color = this.getClientColor(clientId);
	var li = $('<li></li>').addClass('user-badge-item');
	var badge = $('<span></span>')
			.css('background', color)
			.addClass('badge');
	badge.text(username);
	li.append(badge);
	this.userArea.append(li);
	this.clientBadges[clientId] = li;
};

UIElementController.prototype.removeUserBadge = function(clientId) {
	if (!(clientId in this.clientBadges))
		return; //no client badge
	var badge = this.clientBadges[clientId];
	badge.remove();
	delete this.clientBadges[clientId];
};

UIElementController.prototype.removeCaret = function(clientId) {
	if (!(clientId in this.clientCarets))
		return; //no client caret
	var caret = this.clientCarets[clientId];
	caret.remove();
	delete this.clientCarets[clientId];
};

UIElementController.prototype.setCaretPosition = function(clientId, position) {
	var caret = this.getClientCaret(clientId);
	var color = this.getClientColor(clientId);
	this.fakeArea.innerHTML = this.textArea.val().substring(0, position);
	
	var realElement = this.textArea[0];
	var caretElement = caret[0]; //get DOM element from jquery object
	var offset = UIElementController.getAbsolutePosition(this.textArea[0]);
	
	var rects = this.fakeArea.getClientRects();
	var lastRect = rects[rects.length - 1];

	var x = lastRect.left + lastRect.width - offset[0] + document.body.scrollLeft,
		y = lastRect.top - realElement.scrollTop - offset[1] + document.body.scrollTop;

	caretElement.style.cssText = "background: " + color +  "; top: " + y + "px; left: " + x + "px";
};

UIElementController.getAbsolutePosition = function(e) {
	var x = 0;
	var y = 0;
	while (e.offsetParent !== null){
		x += e.offsetLeft;
		y += e.offsetTop;
		e = e.offsetParent;
	}
	return [x, y];
};

UIElementController.getCaretPosition = function(e) {
	if (e.selectionStart) {
		return e.selectionStart;
	} else if (document.selection) {
		var r = document.selection.createRange();
		if (r === null)
			return 0;

		var re = e.createTextRange(), rc = re.duplicate();
		re.moveToBookmark(r.getBookmark());
		rc.setEndPoint('EndToStart', re);

		return rc.text.length;
	}
	return 0;
};

UIElementController.prototype.reloadPage = function() {
	this.reloadModal.modal('show');
};