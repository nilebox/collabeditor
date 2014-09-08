package ru.nilebox.collabedit.transform;

import ru.nilebox.collabedit.operations.OperationBatch;
import ru.nilebox.collabedit.operations.Operation;
import ru.nilebox.collabedit.operations.InsertOperation;
import ru.nilebox.collabedit.operations.RetainOperation;
import ru.nilebox.collabedit.operations.DeleteOperation;

/**
 *
 * @author nile
 */
public class ContentManager {

	private final StringBuilder contentBuilder;

	public ContentManager(String content) {
		this.contentBuilder = new StringBuilder();
		if (content != null) {
			this.contentBuilder.append(content);
		}
	}

	public String getContent() {
		return contentBuilder.toString();
	}

	public void applyOperations(OperationBatch operations) {
		int index = 0;
		for (Operation op : operations) {
			if (op instanceof RetainOperation) {
				RetainOperation retain = (RetainOperation) op;
				index += retain.getLength();
				continue;
			} else if (op instanceof InsertOperation) {
				InsertOperation insert = (InsertOperation) op;
				contentBuilder.insert(index, insert.getText());
				index += insert.getLength();
			} else if (op instanceof DeleteOperation) {
				DeleteOperation delete = (DeleteOperation) op;
				contentBuilder.delete(index, index + delete.getLength());
			}
		}
	}

	public static int transformCaret(int caret, OperationBatch operations) {
		int index = 0;
		for (int i = 0; i < operations.size(); i++) {
			Operation op = operations.get(i);
			if (op instanceof RetainOperation) {
				index += op.getLength();
			} else if (op instanceof InsertOperation) {
				if (index < caret) {
					caret = caret + op.getLength();
				}
			} else if (op instanceof DeleteOperation) {
				if (index < caret) {
					caret = caret - Math.min(op.getLength(), caret - index);
				}
			}
		}
		return caret;
	}

	public static int getRemoteCaret(OperationBatch operations) {
		int index = 0;
		for (int i = 0; i < operations.size(); i++) {
			Operation op = operations.get(i);
			if (op instanceof RetainOperation) {
				index += op.getLength();
			} else if (op instanceof InsertOperation) {
				index += op.getLength();
			} else if (op instanceof DeleteOperation) {
				// delete operation doesn't move cursor
			}			
		}
		return index;
	}

	;	

	@Override
	public String toString() {
		return contentBuilder.toString();
	}
}
