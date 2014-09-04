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
							'deleteCount': 1
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

function notifyReceive(docinfo) {
	_docversion = docinfo.version;
	_oldVal = docinfo.contents;
	_textarea.val(docinfo.contents);
}