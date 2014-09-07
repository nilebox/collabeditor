package ru.nilebox.collabedit.operations;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.annotation.Autowired;
import ru.nilebox.collabedit.dao.DocumentRepository;
import ru.nilebox.collabedit.model.Document;
import ru.nilebox.collabedit.model.TitleUpdate;
import ru.nilebox.collabedit.old.transform.OperationHistory;
import ru.nilebox.collabedit.transform.ContentManager;
import ru.nilebox.collabedit.transform.DocumentTransformer;
import ru.nilebox.collabedit.transform.OperationBatchHistory;
import ru.nilebox.collabedit.transform.TransformationException;

/**
 *
 * @author nile
 */
public class DocumentManager {
	@Autowired
	DocumentRepository docRepo;
	
	private ConcurrentMap<Long, Document> docs = new ConcurrentHashMap<Long, Document>();
	private ConcurrentMap<Long, OperationBatchHistory> docHistory = new ConcurrentHashMap<Long, OperationBatchHistory>();
	private ConcurrentMap<Long, ContentManager> contents = new ConcurrentHashMap<Long, ContentManager>();
	
	public void applyTitle(TitleUpdate update) {
		Document doc = getDocument(update.getDocumentId());
		synchronized(this) {
			doc.setTitle(update.getTitle());
			doc = docRepo.save(doc);
			docs.put(update.getDocumentId(), doc);			
		}
	}
	
	public void applyBatch(Long documentId, OperationBatch batch) throws TransformationException {
		OperationBatchHistory history = getBatchHistory(documentId);
		List<OperationBatch> diffBatches = history.getBatchesForDifference(batch.getDocumentVersion());
		for (OperationBatch b : diffBatches) {
			// transform B to B'
			batch = DocumentTransformer.transformBatches(batch, b).getFirst();
		}
		
		ContentManager contentManager = getContentManager(documentId);
		contentManager.applyOperations(batch);
		
		Document doc = getDocument(documentId);
		synchronized(this) {
			//TODO: bufferize in memory, flush to database by timeout?
			doc.setContents(contents.toString());
			doc.setVersion(doc.getVersion() + 1);
			doc = docRepo.save(doc);
			docs.put(documentId, doc);
			batch.setDocumentVersion(doc.getVersion());
		}
		
		// add to history - may be needed for "merging" edits from multiple clients
		history.addBatch(batch);
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
	
	private OperationBatchHistory getBatchHistory(Long docId) {
		OperationBatchHistory history = docHistory.get(docId);
		if (history == null) {
			Document doc = getDocument(docId);
			OperationBatchHistory newHistory = new OperationBatchHistory();
			history = docHistory.putIfAbsent(docId, newHistory);
			if (history == null)
				history = newHistory;
		}
		return history;
	}
	
	private ContentManager getContentManager(Long docId) {
		ContentManager content = contents.get(docId);
		if (content == null) {
			Document doc = getDocument(docId);
			ContentManager newContent = new ContentManager(doc.getContents());
			content = contents.putIfAbsent(docId, newContent);
			if (content == null)
				content = newContent;
		}
		return content;
	}	
	
}
