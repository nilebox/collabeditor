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

	private final Long documentId;
	private final DocumentRepository docRepo;
	
	private Document document;
	private final OperationBatchHistory history = new OperationBatchHistory();
	private ContentManager contentManager;
	
	public DocumentManager(Long documentId, DocumentRepository docRepo) {
		this.documentId = documentId;
		this.docRepo = docRepo;
	}
	
	public void applyTitle(TitleUpdate update) {
		Document doc = getDocument();
		synchronized(this) {
			doc.setTitle(update.getTitle());
			doc = docRepo.save(doc);
			this.document = doc;
		}
	}
	
	public void applyBatch(Long documentId, OperationBatch batch) throws TransformationException {
		List<OperationBatch> diffBatches = history.getBatchesForDifference(batch.getDocumentVersion());
		for (OperationBatch b : diffBatches) {
			// transform B to B'
			batch = DocumentTransformer.transformBatches(batch, b).getFirst();
		}
		
		ContentManager contentManager = getContentManager();
		contentManager.applyOperations(batch);
		
		Document doc = getDocument();
		synchronized(this) {
			//TODO: bufferize in memory, flush to database by timeout?
			doc.setContents(contentManager.toString());
			doc.setVersion(doc.getVersion() + 1);
			doc = docRepo.save(doc);
			this.document = doc;
			batch.setDocumentVersion(doc.getVersion());
		}
		
		// add to history - may be needed for "merging" edits from multiple clients
		history.addBatch(batch);
	}
	
	public Document getDocument() {
		if (document == null) {
			document = docRepo.findOne(documentId);
		}
		return document;
	}	
	
	private ContentManager getContentManager() {
		if (contentManager == null) {
			Document doc = getDocument();
			contentManager = new ContentManager(doc.getContents());
		}
		return contentManager;
	}	
	
}
