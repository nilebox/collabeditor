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
		DocumentManager manager = managers.get(documentId);
		if (manager == null) {
			DocumentManager newManager = new DocumentManager(documentId, docRepo);
			manager = managers.putIfAbsent(documentId, newManager);
			if (manager == null)
				manager = newManager;
		}
		return manager;
	}	

}
