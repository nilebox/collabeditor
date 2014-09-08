package ru.nilebox.collabedit.operations;

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
public class DocumentManagerRepository {
	
	@Autowired
	DocumentRepository docRepo;
	
	private ConcurrentMap<Long, DocumentManager> managers = new ConcurrentHashMap<Long, DocumentManager>();
	
	public DocumentManager getDocumentManager(Long documentId) {
		DocumentManager doc = managers.get(documentId);
		if (doc == null) {
			DocumentManager newDoc = new DocumentManager(documentId, docRepo);
			doc = managers.putIfAbsent(documentId, newDoc);
			if (doc == null)
				doc = newDoc;
		}
		return doc;
	}	

}
