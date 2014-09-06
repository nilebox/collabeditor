var _textarea = null;
var _titleField = null;
var _stompClient = null;
var _docid = null;
var _oldVal = null;
var _oldTitle = null;
var _docversion = null;
var _clientId = uuid.v4();
var _operationQueue = new queue();
var _lastOperationIndex = -1;
var _pendingOperationIndex = null;

String.prototype.insert = function(index, string) {
	if (index > 0)
		return this.substring(0, index) + string + this.substring(index, this.length);
	else
		return string + this;
};

function attachTextArea(stompClient, docid, docversion, textarea, title) {
	_stompClient = stompClient;
	_docid = docid;
	_docversion = docversion;
	_textarea = textarea;
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
	var op = {'documentId': _docid,
		'clientId': _clientId,
		'type': 'Delete',
		'position': start,
		'deleteCount': length};
	addOperation(op);
}

function notifyInsert(start, text) {
	console.log("insert: start=" + start + ", text=" + text);
	var newText = _oldVal.insert(start, text);
	console.log("new text: " + newText);

	//send operation to server
	var op = {'documentId': _docid,
		'clientId': _clientId,
		'type': 'Insert',
		'position': start,
		'insertedText': text};
	addOperation(op);
}

function addOperation(op) {
	// Increment operation index (needed for futher check of approved operations)
	_lastOperationIndex++;
	console.log("Operation: " + op);
	op.version = _docversion;
	op.operationIndex = _lastOperationIndex;
	if (_pendingOperationIndex === null) {
		// Immediately send the operation to server
		_pendingOperationIndex = _lastOperationIndex;
		stompSend(stompClient, op);
	} else {
		// TODO: merge with latest operation in queue of the same type
		// Add operation to queue
		_operationQueue.enqueue(op);
	}
}

function sendOperationFromQueue() {
	console.log('queue: ' + _operationQueue);
	if (_operationQueue.size() > 0) {
		var op = _operationQueue.dequeue();
		op.version = _docversion;
		_pendingOperationIndex = op.operationIndex;
		stompSend(stompClient, op);
	}
}

function transformRemoteOperation(remoteOp) {
	//TODO: transform remoteOp and local operations in queue
	var index;
	for (index = 0; index < _operationQueue.size(); index++) {
		var op = _operationQueue.get(index);
		op.version = remoteOp.version;
		// remember original positions
		var opPosition = op.position;
		var remoteOpPosition = remoteOp.position;
		// apply transformation to remote operation
		transformWith(remoteOp, remoteOpPosition, op, opPosition);
		// apply transformation to local operation
		transformWith(op, opPosition, remoteOp, remoteOpPosition);
	}
}

function transformWith(originalOp, originalOpPosition, transformOp, transformOpPosition) {
	if (transformOp.position < 0)
		return; //skipped operation	

	switch (transformOp.type) {
		case 'Insert':
			if (originalOpPosition >= transformOpPosition) {
				originalOp.position += transformOp.insertedText.length;
			}
			break;
		case 'Delete':
			var deleteIntersects = false;
			if (originalOpPosition.type === 'Delete') {
				// if current operation's start index is in range of op's delete operation
				if (originalOpPosition >= transformOpPosition && originalOpPosition < transformOpPosition + transformOp.deleteCount) {
					deleteIntersects = true;
					var oldPosition = originalOp.position;
					originalOp.position = transformOpPosition + transformOp.deleteCount;
					originalOp.deleteCount -= originalOp.position - oldPosition;
				}
				var endIndex = originalOp.position + originalOp.deleteCount - 1;
				// if current operation's end index is in range of op's delete operation
				if (originalOpPosition >= transformOpPosition && originalOpPosition < transformOpPosition + transformOp.deleteCount) {
					deleteIntersects = true;
					endIndex = transformOpPosition.position - 1;
					originalOpPosition.deleteCount = endIndex - originalOp.position + 1;
				}
				if (originalOp.deleteCount <= 0 || originalOp.position < 0) {
					originalOp.position = -1;
					originalOp.deleteCount = 0;
					return;
				}
			}

			if (!deleteIntersects && originalOpPosition >= transformOpPosition) {
				originalOp.position -= transformOp.deleteCount;
			}
			break;
		default:
			console.error("Invalid operation type: " + transformOp.type);
	}
}

function remoteNotify(op) {
	_docversion = op.newVersion;

	if (op.clientId === _clientId) {
		if (op.operationIndex !== _pendingOperationIndex) {
			console.error("Invalid operation index, not equal to pending: " + _pendingOperationIndex);
			return;
		}
		console.log("Received operation confirmation: " + _pendingOperationIndex);
		_pendingOperationIndex = null;
		sendOperationFromQueue();
		return;
	}

	if (op.position < 0)
		return; //skipped operation

	switch (op.type) {
		case 'Insert':
			remoteInsert(op.position, op.insertedText);
			break;
		case 'Delete':
			remoteRemove(op.position, op.deleteCount);
			break;
		default:
			console.error("Invalid operation type: " + op.type);
	}
}

function replaceText(newText, transformCursor) {
	var elem = _textarea[0]; //get DOM element from jquery object
	if (transformCursor) {
		var newSelection = [transformCursor(elem.selectionStart), transformCursor(elem.selectionEnd)];
	}

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
}
;

function remoteInsert(pos, text) {
	var transformCursor = function(cursor) {
		return pos < cursor ? cursor + text.length : cursor;
	};

	// Remove any window-style newline characters. Windows inserts these, and
	// they mess up the generated diff.
	var prev = _textarea.val().replace(/\r\n/g, '\n');
	replaceText(prev.slice(0, pos) + text + prev.slice(pos), transformCursor);
}

function remoteRemove(pos, length) {
	var transformCursor = function(cursor) {
		// If the cursor is inside the deleted region, we only want to move back to the start
		// of the region. Hence the Math.min.
		return pos < cursor ? cursor - Math.min(length, cursor - pos) : cursor;
	};

	var prev = _textarea.val().replace(/\r\n/g, '\n');
	replaceText(prev.slice(0, pos) + prev.slice(pos + length), transformCursor);
}

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