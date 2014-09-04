package ru.nilebox.collabedit.transform;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nilebox.collabedit.dao.DocumentRepository;
import ru.nilebox.collabedit.model.Document;

/**
 *
 * @author nile
 */
@Service
public class DiffProcessor {
	
	@Autowired
	DocumentRepository docRepo;
	
	private ConcurrentMap<Long, Document> docs = new ConcurrentHashMap<Long, Document>();
	private ConcurrentMap<Long, OperationHistory> docHistory = new ConcurrentHashMap<Long, OperationHistory>();
	private ConcurrentMap<Long, StringBuilder> docContents = new ConcurrentHashMap<Long, StringBuilder>();
	
	public void applyDiff(Diff diff) throws TransformException {
		Operation operation = diff.getOperation();
//		OperationHistory history = getOperationHistory(diff.getId());
//		List<Operation> diffOperations = history.getOperationsForDifference(diff.getOperation());
//		for (Operation op : diffOperations) {
//			operation.transformWith(op);
//		}
		
		StringBuilder contents = getDocContents(diff.getId());
		switch(operation.getType()) {
			case Insert:
				contents.insert(operation.getPosition(), operation.getInsertedText());
				break;
			case Delete:
				contents.delete(operation.getPosition(), operation.getPosition() + operation.getDeleteCount());
				break;
			default:
				throw new AssertionError(operation.getType().name());
		}
		
		Document doc = getDocument(diff.getId());
		synchronized(this) {
			doc.setContents(contents.toString());
			doc.setVersion(doc.getVersion() + 1);
			doc = docRepo.save(doc);
			docs.put(diff.getId(), doc);
			diff.getOperation().setVersion(doc.getVersion());
		}
	}
	
	public Document getDocument(Long docId) {
		Document doc = docs.get(docId);
		if (doc == null) {
			Document newDoc = docRepo.findOne(docId);
			doc = docs.putIfAbsent(docId, newDoc);
			if (doc == null)
				doc = newDoc;
		}
		return doc;
	}
	
	private OperationHistory getOperationHistory(Long docId) {
		OperationHistory history = docHistory.get(docId);
		if (history == null) {
			Document doc = getDocument(docId);
			OperationHistory newHistory = new OperationHistory(doc);
			history = docHistory.putIfAbsent(docId, newHistory);
			if (history == null)
				history = newHistory;
		}
		return history;
	}
	
	private StringBuilder getDocContents(Long docId) {
		StringBuilder contents = docContents.get(docId);
		if (contents == null) {
			Document doc = getDocument(docId);
			StringBuilder newContents = new StringBuilder();
			if (doc.getContents() != null)
				newContents.append(doc.getContents());
			contents = docContents.putIfAbsent(docId, newContents);
			if (contents == null)
				contents = newContents;
		}
		return contents;
	}
}
