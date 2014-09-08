var _container = null;
var _fakearea = null;
var _textarea = null;
var _userlist = null;
var _titleField = null;
var _stompClient = null;
var _docid = null;
var _oldVal = null;
var _oldTitle = null;
var _docversion = null;
var _clientId = null;
var _batchBuffer = new OperationBatchBuffer();
var _pendingRequestId = null;
var _clientColors = {};
var _clientCarets = {};
var _clientBadges = {};

function attachTextArea(clientId, stompClient, docid, docversion, container, textarea, fakearea, title, userlist) {
	_clientId = clientId;
	_stompClient = stompClient;
	_docid = docid;
	_docversion = docversion;
	_container = container;
	_fakearea = fakearea[0].firstChild;
	_textarea = textarea;
	_userlist = userlist;
	_titleField = title;
	_oldVal = textarea.val();
	_oldTitle = title.text();
	_titleField.on('save', onTitleChanged);
	_textarea.on('change keyup keydown cut paste textInput', onTextChanged);
}

function onTextChanged() {
	var newVal = $(this).val();
	if (newVal === _oldVal) {
		return; //check to prevent multiple simultaneous triggers
	}

	var commonStart = 0;
	while (_oldVal.charAt(commonStart) === newVal.charAt(commonStart)) {
		commonStart++;
	}

	var commonEnd = 0;
	while (_oldVal.charAt(_oldVal.length - 1 - commonEnd) === newVal.charAt(newVal.length - 1 - commonEnd) &&
			commonEnd + commonStart < _oldVal.length && commonEnd + commonStart < newVal.length) {
		commonEnd++;
	}

	if (_oldVal.length !== commonStart + commonEnd) {
		notifyRemove(commonStart, _oldVal.length - commonStart - commonEnd);
	}
	if (newVal.length !== commonStart + commonEnd) {
		notifyInsert(commonStart, newVal.slice(commonStart, newVal.length - commonEnd));
	}
	_oldVal = newVal;
	//action to be performed on textarea changed
//	stompSend(_stompClient, _docid, null, newVal);
}

function notifyRemove(start, length) {
	console.log("remove: start=" + start + ", length=" + length);
	var removedText = _oldVal.substring(start, start + length);
	console.log("removed text: " + removedText);
	var newText = _oldVal.substring(0, start) + _oldVal.substring(start + length);
	console.log("new text: " + newText);

	//send operation to server
	var batch = new OperationBatch(_docversion);
	batch.add(OperationContainer.createRetain(start));
	batch.add(OperationContainer.createDelete(length));
	addBatch(batch);
}

function notifyInsert(start, text) {
	console.log("insert: start=" + start + ", text=" + text);
	var newText = _oldVal.insert(start, text);
	console.log("new text: " + newText);

	//send operation to server
	var batch = new OperationBatch(_docversion);
	batch.add(OperationContainer.createRetain(start));
	batch.add(OperationContainer.createInsert(text));
	addBatch(batch);
}

function addBatch(batch) {
	console.log("Operation: " + batch);
	batch.baseDocumentVersion = _docversion;
	if (_pendingRequestId === null) {
		// Immediately send the operation to server
		sendBatch(batch);
	} else {
		// TODO: merge with latest operation in queue of the same type
		// Add operation to queue
		_batchBuffer.enqueue(batch);
	}
}

function sendBatch(batch) {
	var request = new DocumentChangeRequest(_clientId, _docid, batch);
	_pendingRequestId = request.requestId;
	stompSendOperation(_stompClient, request);
}

function sendBatchFromBuffer() {
	console.log('queue: ' + _batchBuffer);
	if (_batchBuffer.size() > 0) {
		var batch = _batchBuffer.dequeue();
		batch.documentVersion = _docversion;
		sendBatch(batch);
	}
}

function remoteNotify(obj) {
	console.log("notification: " + obj);
	var notification = new DocumentChangeNotification(obj);
	_docversion = notification.newDocumentVersion;

	if (notification.clientId === _clientId) {
		if (notification.requestId !== _pendingRequestId) {
			console.error("Invalid operation index, not equal to pending: " + _pendingRequestId);
			return;
		}
		console.log("Received operation confirmation: " + _pendingRequestId);
		_pendingRequestId = null;
		sendBatchFromBuffer();
		return;
	}
	
	var remoteBatch = transformRemoteBatch(notification.batch);
	applyRemoteBatch(remoteBatch);
	showRemoteCursor(notification.clientId, notification.username, remoteBatch);	
}

function transformRemoteBatch(remoteBatch) {
	for (i = 0; i < _batchBuffer.size(); i++) {
		var batch = _batchBuffer.get(i);
		var transformed = OperationTransformer.transformBatches(remoteBatch, batch);
		remoteBatch = transformed.first;
		batch = transformed.second;
		batch.baseDocumentVersion = remoteBatch.baseDocumentVersion + 1;
		_batchBuffer.set(i, batch);
	}
	return remoteBatch;
}

function applyRemoteBatch(batch) {
	var elem = _textarea[0]; //get DOM element from jquery object

	var oldText = _textarea.val().replace(/\r\n/g, '\n');
	var newText = ContentManager.applyOperations(oldText, batch);
	
	var newSelection = [ContentManager.transformCursor(elem.selectionStart, batch), ContentManager.transformCursor(elem.selectionEnd, batch)];

	// Fixate the window's scroll while we set the element's value. Otherwise
	// the browser scrolls to the element.
	var scrollTop = elem.scrollTop;
	elem.value = newText;
	_oldVal = elem.value;
	if (elem.scrollTop !== scrollTop)
		elem.scrollTop = scrollTop;

	// Setting the selection moves the cursor. We'll just have to let your
	// cursor drift if the element isn't active, though usually users don't
	// care.
	if (newSelection && window.document.activeElement === elem) {
		elem.selectionStart = newSelection[0];
		elem.selectionEnd = newSelection[1];
	}
};

function onTitleChanged(e, params) {
	var newTitle = params.newValue;
	if (newTitle === _oldTitle) {
		return; //check to prevent multiple simultaneous triggers
	}
	
	_oldTitle = newTitle;
	
	var titleUpdate = {'documentId': _docid,
		'clientId': _clientId,
		'title': newTitle};
	
	stompSendTitle(_stompClient, titleUpdate);
}

function remoteTitleUpdate(titleUpdate) {
	if (titleUpdate.clientId === _clientId)
		return; // skip own updates
	_oldTitle = titleUpdate.title;
	_titleField.editable('setValue', titleUpdate.title);
}

function showRemoteCursor(clientId, username, batch) {
	addUserBadge(clientId, username);
	var remoteCursor = ContentManager.getRemoteCursor(batch);
	setCaretPosition(clientId, remoteCursor);
}

function getClientColor(clientId) {
	if (clientId in _clientColors) {
		return _clientColors[clientId];
	}
	var color = Colors.next();
	_clientColors[clientId] = color;
	return color;
}

function createCaret(color) {
	return $('<div></div>')
//			.attr({background: color})
			.addClass('fake_caret blink');
}

function getClientCaret(clientId) {
	if (clientId in _clientCarets) {
		return _clientCarets[clientId];
	}
	var caret = createCaret(getClientColor(clientId));
	//add created cursor to DOM tree
	_container.append(caret);
	//save to map
	_clientCarets[clientId] = caret;
	return caret;
}

function addUserBadge(clientId, username) {
	if (clientId in _clientBadges)
		return; //already exists
	var color = getClientColor(clientId);
	var li = $('<li></li>').addClass('user-badge-item');
	var badge = $('<span></span>')
			.css('background', color)
			.addClass('badge');
	badge.text(username);
	li.append(badge);
	_userlist.append(li);
	_clientBadges[clientId] = li;
}

function setCaretPosition(clientId, position) {
	var caret = getClientCaret(clientId);
	var color = getClientColor(clientId);
	_fakearea.innerHTML = _textarea.val().substring(0, position).replace(/\n$/, '\n\u0001');
	setCaretXY(_fakearea, _textarea[0], caret[0], getPos(_textarea[0]), color);
}

function setCaretXY(elem, realElement, caret, offset, color) {
	var rects = elem.getClientRects();
	var lastRect = rects[rects.length - 1];

	var x = lastRect.left + lastRect.width - offset[0] + document.body.scrollLeft,
		y = lastRect.top - realElement.scrollTop - offset[1] + document.body.scrollTop;

	caret.style.cssText = "background: " + color +  "; top: " + y + "px; left: " + x + "px";
	//console.log(x, y, offset);
}

function getPos(e) {
	var x = 0;
	var y = 0;
	while (e.offsetParent !== null){
		x += e.offsetLeft;
		y += e.offsetTop;
		e = e.offsetParent;
	}
	return [x, y];
}