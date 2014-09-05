var _textarea = null;;
var _stompClient = null;
var _docid = null;
var _oldVal = null;
var _docversion = null;
var _clientId = uuid.v4();

String.prototype.insert = function (index, string) {
  if (index > 0)
    return this.substring(0, index) + string + this.substring(index, this.length);
  else
    return string + this;
};

function attachTextArea(stompClient, docid, docversion, textarea) {
	_stompClient = stompClient;
	_docid = docid;
	_docversion = docversion;
	_textarea = textarea;
	_oldVal = textarea.val();
	_textarea.on('change keyup keydown cut paste textInput', onTextChanged);
}

function onTextChanged() {
	var newVal = $(this).val();
	if(newVal === _oldVal) {
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
	var removedText = _oldVal.substring(start, start+length);
	console.log("removed text: " + removedText);
	var newText = _oldVal.substring(0, start) + _oldVal.substring(start+length);
	console.log("new text: " + newText);
	
	//send operation to server
	var op = {'id': _docid,
			  'operation': {'clientId': _clientId,
							'type': 'Delete',
							'position': start,
							'deleteCount': length
						    }};
	stompSend(stompClient, op);
}

function notifyInsert(start, text) {
	console.log("insert: start=" + start + ", text=" + text);
	var newText = _oldVal.insert(start, text);
	console.log("new text: " + newText);
	
	//send operation to server
	var op = {'id': _docid,
			  'operation': {'clientId': _clientId,
							'type': 'Insert',
							'position': start,
							'insertedText': text
						    }};
	stompSend(stompClient, op);	
}

function remoteNotify(diff) {
	var op = diff.operation;
	_docversion = op.version;
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
    if (transformCursor) {
      var newSelection = [transformCursor(_textarea.selectionStart), transformCursor(_textarea.selectionEnd)];
    }

    // Fixate the window's scroll while we set the element's value. Otherwise
    // the browser scrolls to the element.
    var scrollTop = _textarea.scrollTop;
    _textarea.val(newText);
    _oldVal = _textarea.val(); // Not done on one line so the browser can do newline conversion.
    if (_textarea.scrollTop !== scrollTop)
		_textarea.scrollTop = scrollTop;

    // Setting the selection moves the cursor. We'll just have to let your
    // cursor drift if the element isn't active, though usually users don't
    // care.
    if (newSelection && window.document.activeElement === _textarea) {
      _textarea.selectionStart = newSelection[0];
      _textarea.selectionEnd = newSelection[1];
    }
  };
  
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